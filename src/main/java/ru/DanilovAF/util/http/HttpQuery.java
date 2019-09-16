package ru.DanilovAF.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.DanilovAF.util.ThreadMSG.SourceMSG;
import ru.DanilovAF.util.ThreadMSG.SynThread;
import ru.DanilovAF.util.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by aleksandr.danilov on 08.04.2019.
 */
public class HttpQuery extends SynThread
{
	private static final Logger log = LoggerFactory.getLogger(HttpQuery.class);

	private Socket oSocket = null;     // Сам сокет
	private InputStreamReader from_server;  // Для чтения с сокета
	private PrintWriter to_server;  // Для записи в сокет, запись производить синхронизировано

	SourceMSG<StringBuffer> list = new SourceMSG<StringBuffer>();

	private String sUrl = "";	// Строка запроса API
	private String sHost = "";					// IP по умолчанию
	private int iPort = 80;									// Порт по умолчанию
	private volatile int countCmd = 0;
	private String auth = null;
	private String user = "";
	private String pass = "";
	private String sQ;  // Тот запрос, который надо отработать

	private int iLeData;	// Блинна блока данных
	private int iLenTransferedData = 0;	// Блинна блока данных, которая уже пришла

	private ArrayList<ArrayList<String>> alHeader = new ArrayList<ArrayList<String>>();   // Параметры, которые идут в заголовок HTTP запроса
	private HashSet<String> hsHeader = new HashSet<String>();   // Параметры, которые идут в заголовок HTTP запроса

	public HttpQuery(String sHost, String user, String pass, String sUrl) {
		this.sUrl = sUrl;
		this.sHost = sHost;
		this.user = user;
		this.pass = pass;
	}

	public HttpQuery(String sHost, String user, String pass) {
		this.sHost = sHost;
		this.user = user;
		this.pass = pass;
	}

	public HttpQuery(String sHost, String sUrl) {
		this.sUrl = sUrl;
		this.sHost = sHost;
	}

	public String getsUrl() {
		return sUrl;
	}

	public void setsUrl(String sUrl) {
		this.sUrl = sUrl;
	}

	public StringBuffer execSyn(String sQuery) {
		log.debug("Запрос к серверу \n" + sQuery);
		HttpListner hListner = new HttpListner();
//		HttpZabbix2 zbx = new HttpZabbix2(this, query, zbListner);
		addListner(hListner);
		sQ = sQuery;
		Thread th = startThisThread();
		try {
			waitStartThread();
			waitStopThread();
		} catch (Exception e) {
			log.error("Ошибка!!! \n" + util.stackTrace(e));
//			e.printStackTrace();
		}
		// zbListner содержит весь вывод
		hListner.popAllMsg();	// Вычерпали весь стек сообщений
		StringBuffer sb = hListner.getAnswer();
		removeListner(hListner);
		return sb;
	}

	private void removeListner(HttpListner hListner) {
		list.removeMsgListener(hListner);
	}

	private void addListner(HttpListner hListner) {
		list.addMsgListener(hListner);
	}

	@Override
	public String toString()
	{
		return sHost + ":" + iPort;
	}

	@Override
	public void run() {
		if (log.isInfoEnabled()) { log.info("Старт потока сокета " + this); }
		doBeforeStart();
		setStart();

		//		while(isNeedWork())	// Пока надо работать
		{
			char[] buffer= new char[1550];
			int chars_read; // Кол-во прочитанных байт
			String as = "";	// Буфер чтения за один раз
			StringBuffer sbFrom = new StringBuffer(1550);	// Накапливаем результат чтения с сокета
			int iPos = 0;
			int iPosB = 0;

			if(sQ != null && !sQ.isEmpty())
			{
				// Построим заголовок запроса
				String sJsonH = getQuery();
				try
				{
					// Открыть сокет
					log.trace("Создаем сокет " + this);
					oSocket = getSocket();

					from_server = new InputStreamReader(oSocket.getInputStream());
					to_server = new PrintWriter(oSocket.getOutputStream());

					oSocket.setSoTimeout(1000); // Таймаут на чтение сокета, для того чтобы не блокировать поток

					if (log.isInfoEnabled()) {log.info("Отправляем в Сокет:\n" + sJsonH); }
					to_server.print(sJsonH);  // Записали в сокет
					to_server.flush();      // Сбросили буфер


					while (isNeedWork())
					{
						try
						{   // Контроль на закрытие потока
							chars_read = from_server.read(buffer);
							if (chars_read == -1)
							{   // Проблема? нет, как только сервер ответит - он закроет соединение
//								throw new IOException("Ошибка!!! Чтение с сокета длинной -1. Это ошибка сокета надо соединиться заново.");
								setNeedWork(false);
								list.sendMSG(sbFrom);
								sbFrom = new StringBuffer();
							}
							if (chars_read > 0)
							{
								as = new String(buffer, 0, chars_read);
								if (log.isTraceEnabled()) { log.trace("Прочитали с сокета:\n" + as); }
								sbFrom.append(as);
								// Поищем там атрибут
								if(iLenTransferedData == 0 && (iPosB = sbFrom.indexOf("\r\n\r\n")) != -1 )	// Если еще не пришел заголовок
								{	// Есть окончание заголовка
									//Найдем в заголовке Content-Length:
									iLeData = 0;
									if((iPos = sbFrom.indexOf("Content-Length:")) != -1)
									{
										int iPosEnd = sbFrom.indexOf("\r\n", iPos);
										if(iPosEnd != -1)
										{
											String sLen = sbFrom.substring(iPos + "Content-Length:".length(), iPosEnd);
											iLeData = (int) util.mcn(sLen, '.');
										}
									}
									iLeData = iLeData == 0 ? 0 : iLeData + iPosB + "\r\n\r\n".length();	// Общая длинна всего пакета, если она равна нулю, то выйдем только по удаленному разрыву соединения
									iLenTransferedData = sbFrom.length();
									list.sendMSG(sbFrom);
									sbFrom = new StringBuffer();
									if(iLeData > 0 && iLenTransferedData >= iLeData)
									{	// Данные пришли можно выходить
										setNeedWork(false);
									}
								} else
								{
									list.sendMSG(sbFrom);
									sbFrom = new StringBuffer();
									if(iLeData > 0 && iLenTransferedData >= iLeData)
									{	// Данные пришли можно выходить
										setNeedWork(false);
									}
								}
							}
						} catch (SocketTimeoutException e)
						{   // Ну таймаут и таймаут - проверим надо ли выхордить Можно сюда вставить таймер по зависанию запроса, если прошло например 10 таймаутов, то пора завершаться с ошибкой
							// !!!!
							int y = 0;
							log.debug("Таймаут в сокете...");
						}
					}
					myCloseSocet();
					log.info("Корректная остановка потока работы с Сокетом!");
				} catch (Exception e)
				{   // Инициализация сокета заново
					log.error(util.stackTrace(e));
					myCloseSocet();
					// Уведомим всех, что соединение сломалось... Код ошибки 2. Смотри описание в MyAstExeption
					StringBuffer sMes = new StringBuffer("Error:2\r\nErrorMes:Error Connect\r\n\r\n");
					list.sendMSG(sbFrom);
				}
			}
			list.removeAllMsgListener(); // отписали всех слушателей
			if (log.isInfoEnabled())
			{
				log.info("Зарешение работы с Сокетом!"); }
			setStop();    // Сказали, что остановились
		}

	}

	public Socket getSocket() throws IOException
	{
		return new Socket(sHost, iPort);
	}

	private String getQuery() {

		StringBuffer sbJsonH = new StringBuffer().append("POST ").append(sUrl).append(" HTTP/1.0").append("\r\n");
//		sbJsonH.append("Host: ").append(sHost).append("\r\n");
		for(ArrayList<String> it: alHeader)
		{
			sbJsonH.append(it.get(0)).append(": ").append(it.get(1)).append("\r\n");
		}
		String encoding = new sun.misc.BASE64Encoder().encode((user + ":" + pass).getBytes());

		sbJsonH.append("Content-Length: ").append(sQ.length()).append("\r\n");
		sbJsonH.append("Authorization: Basic ").append(encoding);    // Авторизация
		sbJsonH.append("\r\n\r\n");                                 // Разделитель заголовка и тела
		sbJsonH.append(sQ);
		return sbJsonH.toString();
	}
	private void myCloseSocet()
	{
		if(oSocket != null)
		{
			try
			{
				oSocket.close();
			} catch (IOException e1)
			{   // Проглотим ошибку
			}
		}
		if(from_server != null)
		{
			try
			{
				from_server.close();
			} catch (IOException e1)
			{   // Проглотим ошибку
			}
		}
		if(to_server != null)
		{
			to_server.close();
		}
		oSocket = null;
		from_server = null;
		to_server = null;
	}

	public boolean setHederOpt(String sKey, String sVal) {
		boolean bRet = false;
		String sDelim = ";";
		if(sKey != null && !sKey.isEmpty()) {
			if(sVal == null)
				sVal = "";
			if(sVal.compareTo("Accept") == 0)
				sDelim = ",";
			if (hsHeader.contains(sKey)) {
				bRet = true;
				for(ArrayList<String> it: alHeader)
				{
					if(it.get(0).compareTo(sKey) == 0)
					{
						it.set(1, it.get(1) + "; " + sVal);
						break;
					}
				}
			} else {
				hsHeader.add(sKey);
				ArrayList<String> alBuf = new ArrayList<String>();
				alBuf.add(sKey);
				alBuf.add(sVal);
				alHeader.add(alBuf);
			}
		}
		return false;
	}
	public void setContentTypeTxtXml()
	{
		setHederOpt("Content-Type", "text/xml");
	}
	public void setContentTypeUTF()
	{
		setHederOpt("Content-Type", "charset=utf-8");
	}
	public void setAcceptSoap()
	{
		setHederOpt("Accept", "application/soap+xml");
	}
	public void setAcceptDime()
	{
		setHederOpt("Accept", "application/dime");
	}
	public void setAcceptRelated()
	{
		setHederOpt("Accept", "multipart/related");
	}
	public void setAcceptText()
	{
		setHederOpt("Accept", "text/*");
	}
	public void setHost()
	{
		setHederOpt("Host", sHost);
	}
	public void setHeaderForSOAP()
	{
		setHederOpt("Content-Type", "text/xml; charset=utf-8");
		setHederOpt("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
		setHederOpt("User-Agent", "Axis/1.4");
		setHederOpt("Host", sHost);
		setHederOpt("Cache-Control", "no-cache");
		setHederOpt("Pragma", "no-cache");
		setHederOpt("SOAPAction", "\"\"");
	}

}
