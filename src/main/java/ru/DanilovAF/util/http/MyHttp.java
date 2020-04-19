package ru.DanilovAF.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.DanilovAF.util.util;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by aleksandr.danilov on 19.09.2019.
 *
 * Задача испольнить запрос и вернуть результат
 * Все происходит в том же потоке, в котором запускаемся.
 * Есть максимальное время исполнения, если таймаут, то в итоге выйдем из функции запроса
 *
 */
public class MyHttp
{
	private static final Logger log = LoggerFactory.getLogger(MyHttp.class);

	private Socket oSocket = null;     // Сам сокет
	private InputStreamReader from_server;  // Для чтения с сокета
	private PrintWriter to_server;  // Для записи в сокет, запись производить синхронизировано

	private String host;

	private int port = 80;
	private int timeExec = 20000;   // Время исполнения
	private int oneTime = 1000;   // Время исполнения одного ожидания

	private ArrayList<ArrayList<String>> alHeader = new ArrayList<ArrayList<String>>();   // Параметры, которые идут в заголовок HTTP запроса
	private HashSet<String> hsHeader = new HashSet<String>();   // Параметры, которые идут в заголовок HTTP запроса

	private String url = null;
	private int metod = 1;  // 1 - GET 2- POST
	private String httpVersion = "1.0";
	private boolean flagSsh = false;

	public MyHttp setMetodPost()
	{
		metod = 2;
		return this;
	}
	public MyHttp setMetodGet()
	{
		metod = 1;
		return this;
	}
	public String getHost()
	{
		return host;
	}


	public MyHttp(String host) {
		this.host = host;
	}

	public boolean isFlagSsh() {
		return flagSsh;
	}

	public void setFlagSsh(boolean flagSsh) {
		this.flagSsh = flagSsh;
	}
	public MyHttp setSsh() {
		this.flagSsh = true;
		port = 443;
		return this;
	}

	/**
	 * Получает содеинения с минимальными настроками безопасности, главное соединиться.
	 * @return
	 * @throws IOException
	 */
	public Socket getSocket() throws IOException
	{
		if(oSocket != null) {
			myCloseSocet();
			oSocket = null;
		}
		if(flagSsh) {
			SshTrustAllSslSocket ts = new SshTrustAllSslSocket();
			oSocket = ts.getNewSslSocket(host, port);
		} else {
			oSocket = new Socket(host, port);
		}
		return oSocket;
	}
	public String getQueryGet(String sUrl) {
		return (getQueryGet(sUrl, null));
	}
	public MyHttp setHttpVer11() {
		httpVersion = "1.1";
		return this;
	}
	/**
	 * Получить http пакет по урлу и телу запроса
	 * @param sUrl
	 * @param sBody
	 * @return
	 */
	public String getQueryGet(String sUrl, String sBody) {

		String sMetod = "GET ";
		if(metod == 2)
			sMetod = "POST ";
		if(httpVersion == null || httpVersion.isEmpty())
			httpVersion = "1.0";

		StringBuffer sbJsonH = new StringBuffer().append(sMetod).append(sUrl).append(" HTTP/").append(httpVersion).append("\r\n");
//		sbJsonH.append("Host: ").append(host).append("\r\n");
		if(alHeader.isEmpty())
			setHeaderHost();
		// Вставим все опции
		for(ArrayList<String> it: alHeader)
		{
			sbJsonH.append(it.get(0)).append(": ").append(it.get(1)).append("\r\n");
		}
		// Если есть тело - добавим сначала Content-Length
		boolean flagBody = false;
		if(sBody != null && !sBody.isEmpty()) {
//			setHeaderContentLength(sBody.length());
			sbJsonH.append("Content-Length: ").append(sBody.length()).append("\r\n");
			flagBody = true;
		}
		// Разделитель заголовка и тела
		sbJsonH.append("\r\n");
		// Тело запроса
		if(flagBody)
			sbJsonH.append(sBody);
		return sbJsonH.toString();
	}

	/**
	 * Выполнение запроса построенному по строке url - член класса
	 *
	 * @return
	 * @throws Exception
	 */
	public StringBuffer execQuery() throws Exception {
		StringBuffer sbRet = new StringBuffer();
		if(url != null) {
			sbRet = executingQuery(getQueryGet(url));
		}
		return sbRet;
	}

	/**
	 * Выполнение запроса, построенного по входному URL и с тем телом, которое также в параметре
	 * @param inUrl
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public StringBuffer execQuery(String inUrl, String body) throws Exception {
		String q = getQueryGet(inUrl, body);
		return executingQuery(q);
	}

	/**
	 * Выполнение запроса с урлом в параметре
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public StringBuffer execQueryUrl(String query) throws Exception {
		String q = getQueryGet(query);
		return executingQuery(q);
	}

	/**
	 * Выполнение запроса с указанным телом, при этом урл берется из члена класса url
	 * @param body
	 * @return
	 * @throws Exception
	 */
	public StringBuffer execQueryBody(String body) throws Exception {
		StringBuffer sbRet = new StringBuffer();
		if(url != null) {
			sbRet = execQuery(url, body);
		}
		return sbRet;
	}

	/**
	 * Выполнение запроса.
	 * Сам запрос содержится во входной строке
	 *
	 * Если sQuery - null или "" то просто пойдет соединение на нужный порт. И получим результат.
	 *
	 * @param sQuery
	 * @return
	 * @throws Exception
	 */
	public StringBuffer executingQuery(String sQuery) throws Exception {

		char[] buffer= new char[1550];
		int chars_read; // Кол-во прочитанных байт
		String as = "";	// Буфер чтения за один раз
		StringBuffer sbFrom = new StringBuffer(1550);	// Накапливаем результат чтения с сокета
		int iPos = 0;
		int iPosB = 0;

		try
		{
			// Открыть сокет
			log.trace("Создаем сокет " + this);
			oSocket = getSocket();

			from_server = new InputStreamReader(oSocket.getInputStream());
			to_server = new PrintWriter(oSocket.getOutputStream());

			oSocket.setSoTimeout(oneTime); // Таймаут на чтение сокета, для того чтобы не блокировать поток
			int maxCount = timeExec / oneTime;

			if(sQuery != null && !sQuery.isEmpty()) {
//				sQuery = getQueryGet(sQuery, sBody);
				log.trace("Отправляем в Сокет:\n" + sQuery);
				to_server.print(sQuery);  // Записали в сокет
				to_server.flush();      // Сбросили буфер
			}
			boolean flagDo = true;
			int i = 0;
			while (flagDo)
			{
				try
				{   // Контроль на закрытие потока
					chars_read = from_server.read(buffer);
					if (chars_read == -1)
					{   // Проблема? нет, как только сервер ответит - он закроет соединение
						// считаем удаленная сторона закрыла соеднение
						log.trace("Закрыл соединение сервер...");
						flagDo = false;
					}
					if (chars_read > 0)
					{
						as = new String(buffer, 0, chars_read);
						log.trace("Прочитали с сокета:\n" + as);
						sbFrom.append(as);
						// Анализ на наличие атрибута длинны сообщения и попытка поймать эту длинну
						int iLen = getHeaderContentLengthAll(sbFrom);
						if(iLen > 0 && sbFrom.length() >= iLen) {
							// Можно рвать соединение - пакет получен
							log.trace("Пришел пакет целиком...");
							flagDo = false;
						}
					}
				} catch (SocketTimeoutException e)
				{   // Ну таймаут и таймаут - проверим надо ли выхордить Можно сюда вставить таймер по зависанию запроса, если прошло например 10 таймаутов, то пора завершаться с ошибкой
					i++;
					if(i > maxCount)
						throw new Exception("Превышено время исполнения запроса.");
					log.trace("Таймаут в сокете...");
				}
			}
			myCloseSocet();
			log.trace("Корректная остановка потока работы с Сокетом!");
		} catch (Exception e)
		{   // Инициализация сокета заново
			log.error(util.stackTrace(e));
			myCloseSocet();
			throw new Exception(e);
		}
		log.trace("Зарешение работы с Сокетом!");
		return sbFrom;
	}

	/**
	 * Возвращаяет всю длинну пакета, которую следует ожидать
	 * @param sbFrom
	 * @return
	 */
	private int getHeaderContentLengthAll(StringBuffer sbFrom) {
		int iPosB = 0;
		int iLeData = -1;
		if(sbFrom != null && (iPosB = sbFrom.indexOf("\r\n\r\n")) != -1 )	// Если еще не пришел заголовок
		{	// Есть окончание заголовка
			//Найдем в заголовке Content-Length:
			int iPos = 0;
			if((iPos = sbFrom.indexOf("Content-Length:")) != -1)
			{
				int iPosEnd = sbFrom.indexOf("\r\n", iPos);
				if(iPosEnd != -1)
				{
					String sLen = sbFrom.substring(iPos + "Content-Length:".length(), iPosEnd);
					iLeData = (int) util.mcn(sLen, '.');
					iLeData = iLeData == 0 ? 0 : iLeData + iPosB + "\r\n\r\n".length();	// Общая длинна всего пакета, если она равна нулю, то выйдем только по удаленному разрыву соединения
				}
			}
		}
		return iLeData;
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

	public MyHttp setHederOpt(String sKey, String sVal) {
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
		return this;
	}
	public MyHttp setHeaderContentTypeTxtXml()
	{
		setHederOpt("Content-Type", "text/xml");
		return this;
	}
	public MyHttp setContentTypeJsonRpc()
	{
		setHederOpt("Content-Type", "application/json-rpc");
		return this;
	}
	public MyHttp setHeaderContentTypeUTF()
	{
		setHederOpt("Content-Type", "charset=utf-8");
		return this;
	}
	public MyHttp setHeaderAcceptSoap()
	{
		setHederOpt("Accept", "application/soap+xml");
		return this;
	}
	public MyHttp setHeaderAcceptDime()
	{
		setHederOpt("Accept", "application/dime");
		return this;
	}
	public MyHttp setHeaderAcceptRelated()
	{
		setHederOpt("Accept", "multipart/related");
		return this;
	}
	public MyHttp setHeaderAcceptText()
	{
		setHederOpt("Accept", "text/*");
		return this;
	}
	public MyHttp setHeaderContentLength(int iLen)
	{
		setHederOpt("Content-Length", "" + iLen);
		return this;
	}
	public MyHttp setHeaderHost()
	{
		setHederOpt("Host", host);
		return this;
	}
	public MyHttp setHeaderForSOAP()
	{
		setHederOpt("Content-Type", "text/xml; charset=utf-8");
		setHederOpt("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
		setHederOpt("User-Agent", "Axis/1.4");
		setHederOpt("Host", host);
		setHederOpt("Cache-Control", "no-cache");
		setHederOpt("Pragma", "no-cache");
		setHederOpt("SOAPAction", "\"\"");
		return this;
	}

	public MyHttp setHeaderPasswordBasic(String sUser, String sPass) {
		String userPassword = sUser + ":" + sPass;
		String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
		setHederOpt("Authorization", "Basic " + encoding);
		return this;
	}

	public boolean isContainHeader(String sKey) {
		if(hsHeader.contains(sKey)) {
			return true;
		}
		return false;
	}

	public MyHttp setHost(String host) {
		this.host = host;
		return this;
	}

	public MyHttp setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public static StringBuffer getBody(StringBuffer sb)
	{
		int iPosB = sb.indexOf("\r\n\r\n");
		sb.delete(0, iPosB + "\r\n\r\n".length());
		return(sb);
	}

}
