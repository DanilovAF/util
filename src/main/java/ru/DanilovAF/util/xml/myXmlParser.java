package ru.DanilovAF.util.xml;


import ru.DanilovAF.util.util;

import java.util.Properties;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 09.10.2009
 * Time: 8:49:52
 * Класс для парсенья XML формата. Строго не придерживается стандарта XML просто основной принцып берет.
 * Для цисковских рапортов можно выставить флаг
 * setFlagNoError(true);
 * Это позволит переваривать HTML рапорта
 * также можно давать функцию setsNamesNoNode(",HR,DL,PRE,DT,");
 * Значит эти узлы не могут быть узлами.
 *
 */
public class myXmlParser
{
	// Буфер в котором находися обрабатываемый поток данных
	String sLine = "";
	// Буфер в который вводится текущий текст
	StringBuffer sChBuf = new StringBuffer(64000);
	// Текущий обрабатываемый символ
	String sCh;
	String sSluzh = "<> \"=/";
	// Текущее название ключа при заполнении опций
	String sPropKey;
	// Текущий узел на заполении
	xNode curNode;
	// Ссылка на начальный класс дерева
	xNode rootNode;
	// Последнее найденное дерево, когда вызывается onEndXml, то  rootNode присваивается в lastTree, затем rootNode = null
	xNode lastTree = new xNode();
	private boolean flagNoError = false;

	public xNode getRootNode()
	{
		return rootNode;
	}

	public void setFlagNoError(boolean flagNoError)
	{
		this.flagNoError = flagNoError;
	}

	/**
	 * Состояние, в котором мы находимся на окончании текущего стека, оно всегда однозначно
	 * 0 это начало и еще никакого xml не началось
	 * 1 внутри тега < Сейчас до разделителя идет название тега
	 * 2 Внутри тега </ Сейчас до разделителя идет название тега
	 * 3 Внутри тега < идет нечто до знака равно
	 * 4 Внутри тега < идет нечто после знака равно до разделителя
	 * 5 В теле тега <a> здесь </a>
	 * 6 Внутри Кавычки после знака равно
	 * 7 Внутри закрывающегося тега после определения названия этого закрывающегося тега
	 */

	int iState;
	private String sNamesNoNode = "";

	public String getsNamesNoNode()
	{
		return sNamesNoNode;
	}

	public void setsNamesNoNode(String sNamesNoNode)
	{
		this.sNamesNoNode = sNamesNoNode;
	}

	public void initC()
	{
		rootNode = null;
		curNode = null;
		sChBuf.setLength(0);	//delete(0, sChBuf.length());
		sPropKey = "";
		iState = 0;
		sLine = "";
	}

	/**
	 * Если было событие onEndXml, то дерево надится в этой переменной, иначе она пуста
	 * @return
	 */
	public xNode getLastTree()
	{
		return lastTree;
	}

	/**
	 * Чтение данных из текстового файла
	 * @param in_F файл из которого читаем
	 * @throws Exception может быть ошибка чтения файла
	 */
	public void inputFile(File in_F) throws Exception
	{
		if(in_F.isFile())
		{
			RandomAccessFile raf = new RandomAccessFile(in_F, "r");
			String sLine;
			while((sLine = raf.readLine()) != null)
			{
//				System.out.println(sLine);
				inputString(sLine);
			}
			raf.close();
		}
	}

	/**
	 * Чтение из файла с сохранением перевода строки
	 * @param in_F
	 * @throws Exception
	 */
	public void inputFile_0A(File in_F) throws Exception
	{
		if(in_F.isFile())
		{
			RandomAccessFile raf = new RandomAccessFile(in_F, "r");
			String sLine;
			while((sLine = raf.readLine()) != null)
			{
//				System.out.println(sLine);
				inputString(sLine + "\n");
			}
			raf.close();
		}
	}

	public void inputFileUTF(File in_F) throws Exception
	{
		if(in_F.isFile())
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(in_F.getCanonicalFile()),	"UTF8"));
			String sLine;
			while((sLine = in.readLine()) != null)
			{
//				System.out.println(sLine);
				inputString(sLine);
			}
			in.close();
		}
	}
	public void inputFileUTF_0A(File in_F) throws Exception
	{
		if(in_F.isFile())
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(in_F.getCanonicalFile()),	"UTF8"));
			String sLine;
			while((sLine = in.readLine()) != null)
			{
//				System.out.println(sLine);
				inputString(sLine + "\n");
			}
			in.close();
		}
	}

	// Функция в которую надо передвать строки или части строк XML формата
	// Она будет заполнять свой буфер и анализировать на возможные XML теги
	// 0 - Начальное положение
	// 1 - Внутри <>
	// 2 - Внутри </> нашли закрывающий тег
	// 3 - внутри <> присвоили название ключа или закончилось одно из свойств в пределах <>, т.е. ждем появления еще одного свойства или закрытия >
	// 4 - поймали нечто до знака = внутри <>
	// 5 - вышли из <> - возможно даже с закрытием тега в виде />
	// 6 - внутри кавычки внутри <>
	// 7 - (Состояние исключено) Внутри </> нашли пробел, и название тега не пустое - т.е. скобку не закрыли, но название закрывающего тега уже получили
	// 7 - поймали пробел после =, при этом зачение значения пустое
	public void inputString(String in_s) throws Exception
	{
		String sBuf;
		sLine += in_s;
		int iIndex = 0;
		while(sLine.length() != 0)
		{
			sCh = sLine.substring(0,1);
			sLine = sLine.substring(1);
			// Поиск служебный символ или нет
			if(sCh.compareTo(" ") == 0)
			{	// Нашли пробел, это или внутри кавычек или разделитель операторов
				if(iState == 1)
				{	// Отделение названия тега
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					if(sChBuf.length() == 0)
					{	// Почему- то пошли пробелы после открывающейся скобки, пропустим их

					} else
					{
						// Проверим, может это исключение и найденное не может быть узлом...
						if(sNamesNoNode.indexOf("," + sChBuf.toString() + ",") != -1)
						{   // Этот узел не надо создавть
							if(curNode == rootNode)
							{
								curNode = null;
								rootNode = null;
								iState = 0;
							} else
							{
								curNode = curNode.getParent();
								iState = 5;
							}
						} else
						{
							curNode.setKey(sChBuf.toString());
							iState = 3;
						}
						sChBuf.delete(0, sChBuf.length());
					}
				} else if(iState == 2)
				{	// Нашли название закрывающего тега
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					if(sChBuf.length() == 0)
					{	// Пробелы после закрывающего тега. Пропустим их
					} else
					{	// Нашли название закрывающегося тега ну и идем до закрывающей кавычки
//						iState = 7;
						iState = 2;	// Ничего не изменим, при этом не будет состояния 7
					}
				} else if(iState == 4)
				{	// Нечто после знака равно закончилось, если не в кавычках, то это оно, далее то что до знака равно
					// Считаем как закрытие кавычки
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					curNode.setProp(sPropKey, sChBuf.toString());
					sChBuf.delete(0, sChBuf.length());
					sPropKey = "";
					iState = 3;		// Нечто до знака равно
					//sChBuf.append(sCh);
				} else if(iState == 5 || iState == 6|| iState == 3)
				{	// Здесь пробелы значат как пробелы
					sChBuf.append(sCh);
				} else if(iState == 0)
				{	// Идет какой-то мусор до начала работы  - очистим его
					sChBuf.append(sCh);
				}
			} else if(sCh.compareTo("<") == 0 && sLine.length() > 1 && sLine.substring(0,1).compareTo("/") ==0)
			{	// Начало закрытия какго-то тега
				if(iState == 5)
				{	// Все что в буфере является данными текущего тега
					if(!(sChBuf.length() == 0))
					{
						util.replaceLR(sChBuf, "\r\n\t", "");
						if(!(sChBuf.length() == 0))
						{
							curNode.setValAppend(sChBuf.toString());
						}
					}
					sChBuf.delete(0, sChBuf.length());
					iState = 2;
					sLine = sLine.substring(1);	// Удалим один символ это символ "/"
				} else
				{	// Ошибка, не может быть закрытия тега не в нутри тега
					throw new Exception("Ошибка! не может быть закрытия тега не в нутри тега");
				}
			} else if(sCh.compareTo("/") == 0 && sLine.length() >= 1 && sLine.substring(0,1).compareTo(">") == 0)
			{	// Закрытие текущего тега
				if(iState == 1)
				{	// название тега закрылось
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					// Проверим, может это исключение и найденное не может быть узлом...
					if(sNamesNoNode.indexOf("," + sChBuf.toString() + ",") != -1)
					{   // Этот узел не надо создавть
						if(curNode == rootNode)
						{
							curNode = null;
							rootNode = null;
							iState = 0;
						} else
						{
							curNode = curNode.getParent();
							iState = 5;
						}
					} else
					{
						curNode.setKey(sChBuf.toString());
						curNode = curNode.getParent();
						iState = 5;
					}
					sChBuf.delete(0, sChBuf.length());
				} else if(iState == 3)
				{	// закрытие тега в стостоянии когда ожидаем нечто до знака равно, если переменная не определена, то можно просто повысить уровень иначе ошибка
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					if(sChBuf.length() == 0)
					{
						sChBuf.delete(0, sChBuf.length());
						curNode = curNode.getParent();
						iState = 5;
					} else
					{
						throw new Exception("Ощибка! в этом месте не может идти закрытие тега.\n" + sLine);
					}
				} else if(iState == 4)
				{	// идет окончание уровня после знака равно. Сначала присвоить свойство и повысить уровень
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					sPropKey = util.replace(sPropKey, "\t\n\r", "");
					if(!(sPropKey.compareTo("") == 0))
					{
						curNode.setProp(sPropKey, sChBuf.toString());
					}
					sChBuf.delete(0, sChBuf.length());
					sPropKey = "";
					curNode = curNode.getParent();
					iState = 5;
				} else
				{	// Ощибка в этом месте не может идти закрытие тега.
					throw new Exception("Ощибка! в этом месте не может идти закрытие тега.\n" + sLine);
				}
				sLine = sLine.substring(1);	// Удалим один символ это символ ">" сразу за символом "/"
			} else if(sCh.compareTo("<") == 0)
			{	// Открытие тега
				if(iState == 5 || iState == 0)
				{	// Внутри тела тега есть еще один тег, создадим новый узел
					if(iState == 0)
					{
						sChBuf.delete(0, sChBuf.length());
					} else
					{
					}
					if(!(sChBuf.length() == 0) && curNode != null)
					{	// Еси не пустое тело, то назначим
						util.replaceLR(sChBuf, "\t\n\r", "");
						if(!(sChBuf.length() == 0))
						{
							curNode.setValAppend(sChBuf.toString());
						}
						sChBuf.delete(0, sChBuf.length());
					}
					if(rootNode == null)
					{
						onBegXml();
						rootNode = new xNode();
						curNode = rootNode;
					} else
					{
						//xNode x = new xNode(curNode);	// Создали новый узел с парентом
						xNode x = new xNode();	// Создали новый узел с парентом
						curNode.add(x);					// Вставили его в дерево
						curNode = x;					// Сменили текущее положение
					}
					iState = 1;
				} else if(iState == 6)
				{	// Внутри кавычки просто символ
					sChBuf.append(sCh);
				} else
				{
					throw new Exception("Ошибка! здесь открытие тега невозможно");
				}
			} else if(sCh.compareTo(">") == 0)
			{	// Закрытие тега
				if(iState == 1)
				{	// ЗАкрытие открывающего тега, в sChBuf его название
					util.replace(sChBuf, "\t\n\r", "");
					// Проверим, может это исключение и найденное не может быть узлом...
					if(sNamesNoNode.indexOf("," + sChBuf.toString() + ",") != -1)
					{   // Этот узел не надо создавть
						if(curNode == rootNode)
						{
							curNode = null;
							rootNode = null;
							iState = 0;
						} else
						{
							curNode = curNode.getParent();
							iState = 5;
						}
					} else
					{
						curNode.setKey(sChBuf.toString());
						iState = 5;
					}
					sChBuf.delete(0, sChBuf.length());
				} else if(iState == 2 || iState == 7)
				{	// Закрытие закрывающего тега, в sChBuf его название
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					if(sChBuf.toString().compareTo(curNode.getKey()) == 0)
					{
						curNode = curNode.getParent();
						sChBuf.delete(0, sChBuf.length());
						iState = 5;
					} else
					{	// Проверка название тега на предмет его учета или неучета
						if(sNamesNoNode.indexOf("," + sChBuf.toString() + ",") != -1)
						{	// Это не тег, его не учитываем Очистим буфер, состояние как внутри тега
							sChBuf.delete(0, sChBuf.length());
							if(rootNode == null)
							{
								iState = 0;	// Еще не зашли ни в какой тег
							} else
							{
								iState = 5;
							}
						} else
						{   // Проверим на флаг noerror - если он в истину, то попытаемся закрыть уровень кровнем ниже...
							if(flagNoError)
							{ // Ижем этот тэг уровнями ниже
								xNode tmpNode = curNode.getParent();
								while(tmpNode != null)
								{
									if(sChBuf.toString().compareTo(tmpNode.getKey()) == 0)
									{	// Сума сойти нашли уровнями ниже
										curNode = tmpNode.getParent();
										sChBuf.delete(0, sChBuf.length());
										iState = 5;
										break;
									}
									tmpNode = tmpNode.getParent();
								}
								if(iState != 5)
								{
//									throw new Exception("Ошибка! закрытие не открытого на этом уровне тега " + sChBuf.toString() + "\n" + rootNode.getXML(true));
									// Была ошибка, если идет такое закрытие - будем его просто игнорировать, мы же в режиме с разрешенным нарушением формата
									int uu = 0;
									sChBuf.delete(0, sChBuf.length());
									iState = 5;
								}
							} else
							{
								// Ошибка закрытие не открытого на этом уровне тега
								throw new Exception("Ошибка! закрытие не открытого на этом уровне тега " + sChBuf.toString() + "\n" + rootNode.getXML(true));
							}
						}
					}
				} else if(iState == 4)
				{	// Закрытие открывающего тега с переменными, в sChBuf после знака равно
					util.replace(sChBuf, "\t\n\r", "");
					sPropKey = util.replace(sPropKey, "\t\n\r", "");
					curNode.setProp(sPropKey, sChBuf.toString());
					sChBuf.delete(0, sChBuf.length());
					sPropKey = "";
					iState = 5;
				} else if(iState == 3 && util.replace(sChBuf, "\t\n\r", "").length() == 0)
				{	// Закрытие открывающего тега с переменными сразу после закрытых кавычек, в sChBuf после знака равно
					//curNode.setProp(sPropKey, sChBuf);
					sChBuf.delete(0, sChBuf.length());
					iState = 5;
				} else if(iState == 6)
				{	// Внутри кавычки просто символ
					sChBuf.append(sCh);
				} else
				{
					throw new Exception("Ошибка! Здесь не может быть символа >\n" + sLine);
				}
			} else if(sCh.compareTo("\"") == 0)
			{	// Отработала кавычка или на открытие или на закрытие
				if(iState == 6)
				{	// Закрытие кавычки
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					curNode.setProp(sPropKey, sChBuf.toString());
					sChBuf.delete(0, sChBuf.length());
					sPropKey = "";
					iState = 3;		// Нечто до знака равно
				} else if(iState == 4)
				{	// Открытие кавычки
					sChBuf.delete(0, sChBuf.length());
					iState = 6;
				} else
				{	// Ошибка в других ситуациях Кавычка не должна встречаться
					sChBuf.append(sCh);
					//throw new Exception("Ошибка! в других ситуациях Кавычка не должна встречаться");
				}
			} else if(sCh.compareTo("=") == 0)
			{	// Отработал знак равно
				if(iState == 3)
				{	// Закончилось нечно до знака равно
					util.replace(sChBuf, "\t\n\r", "");
					util.trimLR(sChBuf, " ");
					sPropKey = sChBuf.toString();
					sChBuf.delete(0, sChBuf.length());
					iState = 4;
				} else if(iState == 6 || iState == 5)
				{	// Внутри кавычки или в теле тега просто символ
					sChBuf.append(sCh);
				}
			} else
			{
				sChBuf.append(sCh);
			}
			// Проверка на закрытие начального XML тега
			if(iState == 5 && curNode == null)
			{	// Закрытие XML
				xNode x = new xNode();	// Создали новый узел
				x.add(rootNode);		// Вставили в него дерево
				//rootNode.setParent(x);

				lastTree = x;	// Сохраним последний найденный рапорт 
				onEndXml(x);
				// Подготовимся к новым подвигам
				rootNode = null;
				x = null;
				curNode = null;
				sChBuf.delete(0, sChBuf.length());
				sPropKey = "";
				iState = 0;
			}
		}
	}

	
	public static void main(String[] args)
	{
		myXmlParser m = new myXmlParser();
		String s = "<table class=dataframe>\n" + "\t<tr>\n" + "\t\t<td>\n" + "\t\t\t<table class=dataout cellspacing=0>\n" + "\t\t\t\t<tr  bgcolor=#A0DCC3>\n" + "\t\t\t\t\t<th width=40%>ФИО, должность, подразделение</th>\n" + "\t\t\t\t\t<th width=10%>Адрес</th>\n" + "\t\t\t\t\t<th width=20%>Телефон</th>\n" + "\t\t\t\t\t<th width=10%>Факс</th>\n" + "\t\t\t\t\t<th colspan=2  width=20%>Эл. почта</th>\n" + "\t\t\t\t</tr>\n" + "\t\t\t\t\t<tr>\n" + "\t\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t\t<a class=empl href=empldet.php?emplid=1776>\n" + "\t\t\t\t\t\t\t\t<font class=emplout>Воробьев Максим Николаевич</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t\t<a href=empls.php?profid=434>Инженер-программист</a>\n" + "\t\t\t\t\t\t\t<a href=depts.php?deptid=457>\n" + "\t\t\t\t\t\t\t\t<font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t\t<a href=depts.php?deptid=456>\n" + "\t\t\t\t\t\t\t\t<font class=deptout>Служба эксплуатации информационных систем</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t</td>\n" + "\t\t\t\t\t\t<td>г. Архангельск, пр. Ломоносова, д.142, каб. 309</td>\n" + "\t\t\t\t\t\t<td align=center>650370</td>\n" + "\t\t\t\t\t\t<td> </td>\n" + "\t\t\t\t\t\t<td colspan=2 align=center>\n" + "\t\t\t\t\t\t\t<a href=mailto:VorobevMN@af.artelecom.ru>VorobevMN@af.artelecom.ru</a>\n" + "\t\t\t\t\t\t</td>\n" + "\t\t\t\t\t</tr>\n" + "\t\t\t\t\t<tr>\n" + "\t\t\t\t\t\t<td colspan=6>\n" + "\t\t\t\t\t\t\t\n" + "\t\t\t\t\t\t</td>\n" + "\t\t\t\t\t</tr>\n" + "\t\t\t\t\t<tr>\n" + "\t\t\t\t\t\t<td>\n" + "\t\t\t\t\t\t\t<a class=empl href=empldet.php?emplid=2134>\n" + "\t\t\t\t\t\t\t\t<font class=emplout>Глизнуца Владислав Анатольевич</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t\t<a href=empls.php?profid=434>Инженер-программист</a>\n" + "\t\t\t\t\t\t\t<a href=depts.php?deptid=457>\n" + "\t\t\t\t\t\t\t\t<font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t\t<a href=depts.php?deptid=456>\n" + "\t\t\t\t\t\t\t\t<font class=deptout>Служба эксплуатации информационных систем</font>\n" + "\t\t\t\t\t\t\t</a>\n" + "\t\t\t\t\t\t</td>\n" + "\t\t\t\t\t\t<td>г. Архангельск, пр. Троицкий, д. 45, каб. 319</td>\n" + "\t\t\t\t\t\t<td align=center>219165</td>\n" + "\t\t\t\t\t\t<td>654096</td>\n" + "\t\t\t\t\t\t<td colspan=2 align=center>\n" + "\t\t\t\t\t\t\t<a href=mailto:vg@main.artelecom.ru>vg@main.artelecom.ru</a>\n" + "\t\t\t\t\t\t</td>\n" + "\t\t\t\t\t</tr>\n" + "\t\t\t\t\t\t<tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2113><font class=emplout>Гыда Марина Николаевна</font></a><a href=empls.php?profid=435>Инженер-программист 1 категории</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Плесецк</td><td align=center>818 32 72543</td><td> </td><td colspan=2 align=center><a href=mailto:st001232@artelecom.ru>st001232@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1730><font class=emplout>Данилов Александр Федорович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр. Ломоносова, д. 144, каб. 609</td><td align=center>650368</td><td> </td><td colspan=2 align=center><a href=mailto:DanilovAF@af.artelecom.ru>DanilovAF@af.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2268><font class=emplout>Дроздов Сергей Николаевич</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр. Троицкий, 45, каб. 319</td><td align=center>219165</td><td>654096</td><td colspan=2 align=center><a href=mailto:dsn@main.artelecom.ru>dsn@main.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1775><font class=emplout>Замятин Юрий Викторович</font></a><a href=empls.php?profid=90>Инженер-электроник</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Ломоносова, 144, каб.202</td><td align=center>650853</td><td>650403</td><td colspan=2 align=center><a href=mailto:st056109@bc.artelecom.ru>st056109@bc.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1735><font class=emplout>Знатных Андрей Юрьевич</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб.124</td><td align=center>215926</td><td>654096</td><td colspan=2 align=center><a href=mailto:regint@artelecom.ru>regint@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2387><font class=emplout>Ипатов Алексей Николаевич</font></a><a href=empls.php?profid=435>Инженер-программист 1 категории</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>165150, г.Вельск, ул.Дзержинского, 46</td><td align=center>818 36 61480</td><td>818 36 65522</td><td colspan=2 align=center><a href=mailto:st020236@velsk.artelecom.ru>st020236@velsk.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1778><font class=emplout>Калинников Сергей Александрович</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр. Ломоносова, д.142, каб. 609</td><td align=center>650370</td><td> </td><td colspan=2 align=center><a href=mailto:KalinnikovSA@af.artelecom.ru>KalinnikovSA@af.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2396><font class=emplout>Кашин Евгений Иванович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>165150 г. Вельск, Дзержинского, 46</td><td align=center>818 36 61480</td><td>818 36 65522</td><td colspan=2 align=center><a href=mailto:st015236@velsk.artelecom.ru>st015236@velsk.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1838><font class=emplout>Климов Дмитрий Николаевич</font></a><a href=empls.php?profid=433>ведущий инженер-электроник</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Котлас, ул. Невского, д. 18</td><td align=center>818 37 27702, 26996</td><td> </td><td colspan=2 align=center><a href=mailto:abondept.kotlas@artelecom.ru>abondept.kotlas@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1834><font class=emplout>Козырев Сергей Юрьевич</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Котлас, ул. Невского, д. 18</td><td align=center>818 37 27702, 26996</td><td> </td><td colspan=2 align=center><a href=mailto:abondept.kotlas@artelecom.ru>abondept.kotlas@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2135><font class=emplout>Комаров Андрей Викторович</font></a><a href=empls.php?profid=590>Инженер-электроник 2 категории</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>164750 гМезень ул.Свободы 3а</td><td align=center>818 48 91607</td><td>818 48 43222</td><td colspan=2 align=center><a href=mailto:admin248@artelecom.ru>admin248@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2136><font class=emplout>Кононов Иван Владимирович</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Каргополь, проспект Октябрьский, д. 57</td><td align=center>818 41 22506</td><td> </td><td colspan=2 align=center><a href=mailto:admin241@artelecom.ru>admin241@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2086><font class=emplout>Крупец Максим Александрович</font></a><a href=empls.php?profid=90>Инженер-электроник</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб.319</td><td align=center>219165</td><td>654096</td><td colspan=2 align=center><a href=mailto:chip@main.artelecom.ru>chip@main.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2411><font class=emplout>Крыжановский Алексей Викторович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр. Ломоносова, д.142, каб. 609</td><td align=center>650370</td><td> </td><td colspan=2 align=center><a href=mailto:KrizhanovskiyAV@af.artelecom.ru>KrizhanovskiyAV@af.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2408><font class=emplout>Кузнецова Светлана Викторовна</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>п. Плесецк</td><td align=center>818 32 71165</td><td> </td><td colspan=2 align=center><a href=mailto:admin232@artelecom.ru>admin232@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1734><font class=emplout>Лащук Игорь Вячеславович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб. 124</td><td align=center>215744</td><td>654096</td><td colspan=2 align=center><a href=mailto:des@artelecom.ru>des@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1728><font class=emplout>Маняк Дмитрий Юрьевич</font></a><a href=empls.php?profid=62>Руководитель группы</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб. 319</td><td align=center>650151</td><td>654096</td><td colspan=2 align=center><a href=mailto:dm@artelecom.ru>dm@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2410><font class=emplout>Медведев Олег Владимирович</font></a><a href=empls.php?profid=435>Инженер-программист 1 категории</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Северодвинск, ул. Кирилкина, д. 8А</td><td align=center>818 45 54249</td><td> </td><td colspan=2 align=center><a href=mailto:graf@sev.artelecom.ru>graf@sev.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1733><font class=emplout>Расщепкин Михаил Павлович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб. 124</td><td align=center>215658</td><td>654096</td><td colspan=2 align=center><a href=mailto:rm@artelecom.ru>rm@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2409><font class=emplout>Сорванов Михаил Леонидович</font></a><a href=empls.php?profid=435>Инженер-программист 1 категории</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Северодвинск, ул. Кирилкина, д. 8А</td><td align=center>818 4 554248 </td><td> </td><td colspan=2 align=center><a href=mailto:sml@sev.artelecom.ru>sml@sev.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1732><font class=emplout>Татарский Валерий Игоревич</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Троицкий, 45, каб. 319</td><td align=center>219165</td><td>654096</td><td colspan=2 align=center><a href=mailto:mrdog@artelecom.ru>mrdog@artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1774><font class=emplout>Трофимов Михаил Ильич</font></a><a href=empls.php?profid=27>Ведущий инженер-электроник</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Архангельск, пр.Ломоносова, 144, каб.202</td><td align=center>650853</td><td>650403</td><td colspan=2 align=center><a href=mailto:st033109@bc.artelecom.ru>st033109@bc.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=1796><font class=emplout>Шевченко Андрей Викторович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Северодвинск, ул. Кирилкина, д. 8А</td><td align=center>818 45 54250</td><td> </td><td colspan=2 align=center><a href=mailto:andy@sev.artelecom.ru>andy@sev.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2076><font class=emplout>Южаков Игорь Рюрикович</font></a><a href=empls.php?profid=28>Ведущий инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Нарьян-Мар</td><td align=center>818 53 42056</td><td> </td><td colspan=2 align=center><a href=mailto:igor@nmues.artelecom.ru>igor@nmues.artelecom.ru</a></td></tr><tr><td colspan=6></td></tr><tr><td><a class=empl href=empldet.php?emplid=2397><font class=emplout>Яббаров Ренат Рафаэльевич</font></a><a href=empls.php?profid=434>Инженер-программист</a><a href=depts.php?deptid=457><font class=deptout>Группа технической эксплуатации вычислительно-сетевых комплексов</font></a><a href=depts.php?deptid=456><font class=deptout>Служба эксплуатации информационных систем</font></a></td><td>г. Няндома, ул. 60 лет Октября, д.60</td><td align=center>818 38 64247</td><td> </td>\n" + "\t\t\t\t\t<td colspan=2 align=center>\n" + "\t\t\t\t\t\t<a href=mailto:admin238@artelecom.ru >admin238@artelecom.ru </a>\n" + "\t\t\t\t\t</td>\n" + "\t\t\t\t</tr>\n" + "\t\t\t</table>\n" + "\t\t</td>\n" + "\t</tr>\n" + "</table>";
		String sLine;

		String a = "\n\r\n\rqwe\n\r\rrty\n\r";
		a = util.replaceL(a, "\n\r", "");
		a= util.replaceR(a, "\n\r", "");
		a= util.replace(a, "\n\r", "");

		try
		{
			m.inputString(s);

			BufferedReader m_br = new BufferedReader(new InputStreamReader(System.in));	// Получили консоль в BufferedReader
			while(true)
			{
				if(m_br.ready())
				{
					sLine = m_br.readLine();
					System.out.println(sLine);
				}
				Thread.yield();
				//System.out.println();
			}
//			File f = new File("D:\\test.xml");
//			RandomAccessFile raf = new RandomAccessFile(f, "rw");
//			String sLine;
//			while(true)
//			{
//				if((sLine = raf.readLine()) == null)
//				{
//					break;
//				}
//				System.out.println(sLine + "\n");
//				m.inputString(sLine);
//			}
		} catch (Exception e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public void startElement(String localName, Properties attributes)
	{

	}
	public void endElement(String localName)
	{

	}
	public void characters(char ch[], int start, int length)
	{

	}

	/**
	 * Функция вызывается всякий раз когда в потоке закончился очередной XML блок верхнего уровня
	 * @param in_n Это верхний узел закончившегося XML блока верхнего уровня
	 */
	public void onEndXml(xNode in_n)
	{
		//System.out.println(in_n.getXML(true));
	}

	/**
	 * Нашли нечто похожее на начало xml тега
	 */
	public void onBegXml()
	{
		
	}
}




