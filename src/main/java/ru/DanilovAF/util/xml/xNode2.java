package ru.DanilovAF.util.xml;

import ru.DanilovAF.util.util;

import java.util.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 26.12.2005
 * Time: 14:11:18
 * Узел для работы с деревьями XML
 * Параметризованый класс в качестве значения узла может быть любой объект
 */
public class xNode2<typeOb> extends ArrayList<xNode2<typeOb>>
{
	/**
	 *  Название элемента XML
	 */
	protected String m_sKey;
	/**
	 * Значение элемента XML
	 */
	protected typeOb m_sVal;
	protected xNode2<typeOb> m_parentNode;
	protected mProp m_propOptions;

	protected void initC()
	{
		m_sKey = "";
		m_sVal = null;
		m_parentNode = null;
		m_propOptions = null;
	}

	/**
	 * Возвращает истину, если все не заполнено, нет детей, нет ключа, нет значения, нет параметров
	 * @return истина если пуст
	 */
	public boolean isEmpty()
	{
		boolean bRet = true;
		if(size() != 0)
		{
			bRet = false;
		} else if(getKey() != null && getKey().compareTo("") != 0)
		{
			bRet = false;
//		} else if(getVal() != null && getVal().compareTo("") != 0)
		} else if(getVal() != null)
		{
			bRet = false;
		} else if(m_propOptions != null && !m_propOptions.isEmpty())
		{
			bRet = false;
		}
		return(bRet);
	}

	public xNode2()
	{
		initC();
	}
//	public xNode(xNode in_This)
//	{
//		initC();
//		m_parentNode = in_This;
//	}

	public xNode2(String in_sKey)
	{
		initC();
		m_sKey = in_sKey;
	}

	/**
	 * Имейте ввиду, что передавая сюда класс, и изменяя этот класс извне - поменяется и в этом классе
	 * @param in_sKey
	 * @param in_sVal
	 */
	public xNode2(String in_sKey, typeOb in_sVal)
	{
		initC();
		m_sKey = in_sKey;
		m_sVal = in_sVal;
	}

	public xNode2<typeOb> getParent()
	{
		return(m_parentNode);
	}

	/**
	 *
	 * @return Возвращает значение данного узла
	 */
	public typeOb getVal()
	{
//		return(m_sVal.toString().replaceAll("&gt;", ">").replaceAll("&lt;", "<"));
		return(m_sVal);
	}

//	/**
//	 * Делает копию значения в StringBuffer через String и возвращает как новый StringBuffer
//	 * @return Значение узла
//	 */
//	public StringBuffer getValCopy()
//	{
//		StringBuffer sbRet = new StringBuffer(m_sVal.length());
//		sbRet.append(m_sVal.toString().replaceAll("&gt;", ">").replaceAll("&lt;", "<"));
//		return(sbRet);
//	}
	/**
	 *
	 * @return Возвращает значение ключа текущего узла
	 */
	public String getKey()
	{
		return(m_sKey);
	}

	/**
	 * Получить свойство у этого узла с именем in_sKeyProp.
	 * @param in_sKeyProp имя свойства
	 * @return значение этого свойства
	 */
	public String getPropVal(String in_sKeyProp)
	{
		String sRet = null;
		if(m_propOptions != null)
		{
			sRet = m_propOptions.getVal(in_sKeyProp);
		}
		return(sRet);
	}
	/**
	 *
	 * @param in_sAdr
	 * @return Значение этого поля в XML на данном уровне (по указанному адресу) адрес относительно текущего узла
	 */
	public typeOb getVal(String in_sAdr)
	{
		xNode2<typeOb> xN = getNodeForAdr(in_sAdr, false);
		typeOb sRet = null;
		if(xN != null)
		{
			sRet = xN.getVal();	// Вернули назад подстановки
		}
		return(sRet);
	}

	/**
	 *
	 * @param in_sAddres
	 * @return Возвращает вектор ключей по указанному адресу
	 */
	public Vector<String> getLevelKeys(String in_sAddres)
	{
		Vector<String> sRet = new Vector<String>();
		return(sRet);
	}

	/**
	 *
	 * @param in_sAddr
	 * @return Возвращает по указноому адресу название ключей и их значение (только текущи уровень)
	 */
	public Properties getLevelProp(String in_sAddr)
	{
		Properties pRet = new Properties();
		return(pRet);
	}

	public xNode2<typeOb> setKey(String in_sKey)
	{
		m_sKey = in_sKey.trim();
		return(this);
	}
	public xNode2<typeOb> setProp(String in_sKey, String sVal)
	{
		if(m_propOptions == null)
			m_propOptions = new mProp();
		m_propOptions.addLast(in_sKey, sVal);
		return(this);
	}
	public xNode2<typeOb> setVal(typeOb in_sVal)
	{
//		m_sVal.delete(0, m_sVal.length()).append(in_sVal.replaceAll(">", "&gt;").replaceAll("<", "&lt;"));	//.trim();
		m_sVal = in_sVal;
		return(this);
	}

//	public xNode2 setValAppend(String in_sVal)
//	{
//		String sBuf = in_sVal.trim().replaceAll(">", "&gt;").replaceAll("<", "&lt;");
//		if(sBuf != null && sBuf.compareTo("") != 0)
//			m_sVal.append(sBuf);
//		return(this);
//	}

	public boolean setVal(String in_sAdr, typeOb in_sVal)
	{
		getNodeForAdr(in_sAdr, true).setVal(in_sVal);
		return(true);
	}
	public boolean addVal(String in_sAdr, typeOb in_sVal)
	{
		xNode2<typeOb> node = getNodeForAdr(in_sAdr, false);
		String sKey = node.getKey();
		if(node != null)
		{
			node = node.getParent();
			xNode2<typeOb> nBuf = new xNode2<typeOb>(sKey);
			nBuf.setVal(in_sVal);
			node.add(nBuf);
		} else
		{
			node = getNodeForAdr(in_sAdr, true);
			node.setVal(in_sVal);
		}
		return(true);
	}

	public xNode2<typeOb> addNode(xNode2<typeOb> o)
	{
		add(o);
		return(o);
	}

	@Override
	public boolean add(xNode2<typeOb> o)
	{
		o.setParent(this);
		return super.add(o);	//To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void add(int index, xNode2<typeOb> element)
	{
		element.setParent(this);
		super.add(index, element);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public void addElement(xNode2<typeOb> obj)
	{
		obj.setParent(this);
		super.add(obj);	//To change body of overridden methods use File | Settings | File Templates.
	}

	public void del(String in_sAdr) throws Exception
	{
		xNode2<typeOb> n = getNodeForAdr(in_sAdr, false);
		if(n == null)
		{	// А ничего делать не надо

		} else
		{	// Есть узел который надо удалить
			if(n == this)
			{	// Вот собака хочет меня удалить, такого быть не должно
				throw new Exception("Невозможно удалить корень дерева!!!");
			} else
			{	// Присвоим в этом узде ссылку на парент в ноль и удалим этот узел из парента
				xNode2<typeOb> nPar = n.getParent();
				n.m_parentNode = null;
				if(nPar != null)
				{
					nPar.remove(n);
				}
				n.clear();
			}
		}
	}

	@Override
	public boolean equals(Object o)
	{
		return(this == o);
	}

	/**
	 * Параметр по умолчанию false
	 * @param in_sAdr адрес как адрес
	 * @return то же что и getNodeForAdr
	 */
	public xNode2<typeOb> getNodeForAdr(String in_sAdr)
	{
		return(getNodeForAdr(in_sAdr, false));
	}
	/**
	 * Поиск (создание) узла по указанному адресу. Нулевых индексов не существует
	 * @param in_sAdr
	 * @param in_bCreate если истина, то при несуществующем адресе узлы создаются
	 * @return сам узел по этому адресу
	 */
	public xNode2<typeOb> getNodeForAdr(String in_sAdr, boolean in_bCreate)
	{
		// делим адресную строку на токены по запятой
		xNode2<typeOb> nodeBuf = null;
		StringTokenizer stAdr = new StringTokenizer(in_sAdr, ",");
		while(stAdr.hasMoreTokens())
		{
			String s=stAdr.nextToken();
			if(s.compareTo("") == 0)	// если пустая строка, то начинаем с корня
			{
				// надо начинать отсчет от текущего элемента
				nodeBuf = this;
			} else if(s.matches("\\d"))	// проверка на содержание в адресе только цифр
			{
				int intPos = new Integer(s);
				if(nodeBuf == null)
					nodeBuf = this;
				else
				{
					if(nodeBuf.size() >= intPos - 1)	// Проверка на попадание в кол-во элементов
						nodeBuf = nodeBuf.get(intPos - 1);	// возвращаем узел по указанному номеру
					else if(in_bCreate)
					{	// Надо создать узлы
						//xNode nodeBuf2 = new xNode(nodeBuf);
						xNode2<typeOb> nodeBuf2 = new xNode2<typeOb>();
						nodeBuf.add(nodeBuf2);
						nodeBuf = nodeBuf2;
					} else
					{
						nodeBuf = null; break;	// выходим с нулевым узлом
					}
				}
			} else
			{	// Надо полагать, что если это не число - то это название тега (Если есть повторяющиеся теги или не
				// обязательные, то мне пофиг - нахожу первый попавшийся  - не нахожу - говорю, что нет) поиск только
				// на следующем уровне - ниже не опускаемся
				boolean bFlagFinde = false;
				if(nodeBuf == null)
					nodeBuf = this;
				for(xNode2<typeOb> nodeBuf1 : nodeBuf)
				{
					if(nodeBuf1.getKey().compareTo(s) == 0)
					{	// Нашли - поиск окончен
						nodeBuf = nodeBuf1;
						bFlagFinde = true;
						break;
					}
				}
				if(!bFlagFinde)
				{
					if(in_bCreate)
					{	// Надо создать узлы
						//xNode nodeBuf2 = new xNode(nodeBuf);
						xNode2<typeOb> nodeBuf2 = new xNode2<typeOb>();
						nodeBuf.add(nodeBuf2);
						nodeBuf2.setKey(s);
						nodeBuf = nodeBuf2;
					} else
					{
						nodeBuf = null; break;	// выходим с нулевым узлом
					}
				}
			}
		}
		// Если начинается с запятой, адрес относительно данного узла если не с запятой, то первый токен должен соответствовать текущему
		// Если стоит флаг in_bCreate, то несуществующие узлы создаются. Если указан номер 3 узла, а предыдцщих названия нет, то создаем узлы с пустым значением и ключем "Param"
		return(nodeBuf);
	}

	public xNode2<typeOb> RemoveAll()
	{
		m_parentNode = null;
		for(xNode2<typeOb> xN: this)
		{
			xN.RemoveAll();
		}
		clear();
		return(this);
	}


	/**
	 * От текущего узла создает XML файл и возвращает XML (рекурсивная функция)
	 * @param flagFormat если истина, то форматировать иначе нет
	 * @return строка XML
	 */
	public String getXML(boolean flagFormat)
	{
		StringBuffer sRet = new StringBuffer();
		if(flagFormat)
		{
			//sRet = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		}

		getXML(sRet, 1, flagFormat);
		sRet = util.replaceR(sRet, "\r\n", "");
		return(sRet.toString());
	}
	private void getXML(StringBuffer sb, int in_intLevel, boolean flagFormat)
	{
		String sSpace = "", sSpace2 = "";
		if(flagFormat)
		{
			sSpace = space(in_intLevel);
			sSpace2 = space(in_intLevel + 1);
		}
		//String sXml = "\r\n" + sSpace + "<" + getKey();
		//String sXml = sSpace + "<" + getKey();
		sb.append(sSpace).append("<").append(getKey());
		if(m_propOptions != null && m_propOptions.size() != 0)
		{
			sb.append(m_propOptions.getProp());
//			for(Object sKeyProp: m_propOptions.keySet())
//			{
//			   //sXml += " " +  sKeyProp.toString() + "=\"" + m_propOptions.getProperty(sKeyProp.toString()) + "\"";
//				sb.append(" ").append(sKeyProp.toString()).append("=\"").append(m_propOptions.getProperty(sKeyProp.toString())).append("\"");
//			}
		}
			//sXml += " " + s + " = \"" + m_propOptions.getProperty(s) + "\"";

		if(size() == 0)
		{	// Пустой список вложений
//			if(getVal() != null && getVal().compareTo("") != 0)
			if(getVal() != null)
			{
				if(flagFormat)
				{
					//sXml += ">" + getVal() + "</" + getKey() + ">\r\n";
					sb.append(">").append(getVal()).append("</").append(getKey()).append(">\r\n");
				} else
				{
					//sXml += ">\r\n" + getVal() + "\r\n</" + getKey() + ">\r\n";
					sb.append(">\r\n").append(getVal()).append("\r\n</").append(getKey()).append(">\r\n");
				}
			} else
			{   // Еще и значение нет
				//sXml += "/>\r\n";
				sb.append("/>\r\n");
			}
		} else
		{
//			if(getVal() != null && getVal().compareTo("") != 0)
			if(getVal() != null)
			{
				//sXml += ">";
				//sXml += "\r\n" + sSpace2 + getVal() + "\r\n";
				sb.append(">" + "\r\n").append(sSpace2).append(getVal()).append("\r\n");
			} else
			{	// Пустое значение
				//sXml += ">\r\n";
				sb.append(">\r\n");
			}
			for(xNode2<typeOb> xN: this)
			{
				//sXml += xN.getXML(in_intLevel + 1, flagFormat);
				xN.getXML(sb, in_intLevel + 1, flagFormat);
			}
			//sXml += sSpace + "</" + getKey() + ">\r\n";
			sb.append(sSpace).append("</").append(getKey()).append(">\r\n");
		}

		//return(sXml);
	}
	public String space(int in_intCount)
	{
		String sBuf = "";
		while(--in_intCount != 0)
		{
			sBuf += "   ";
		}
		return(sBuf);
	}
	public String getCommandIVC()
	{
		String sRet;
		sRet = "";

//		if(getVal() != null && getVal().compareTo("") != 0)
		if(getVal() != null)
		{

			sRet += getKey() + "=" + getVal();
		}
		for(xNode2<typeOb> xN: this)
		{
/*			if(sRet.compareTo("") == 0)
				sRet += xN.getCommandIVC();
			else*/
				sRet += "$" + xN.getCommandIVC();
		}
		return(sRet);
	}

	public void Write(String in_sFileNAme) throws IOException
	{
		Write(in_sFileNAme, true);
	}
	/**
	 * Запись данного класса на диск в указанный файл.
	 * Открывает файл и записывает.
	 */
	public void Write(String in_sFileNAme, boolean in_bAppend) throws IOException
	{
		if(in_sFileNAme != null)
		{
			File fWrite = new File(in_sFileNAme);
			BufferedWriter brFile;
			brFile = new BufferedWriter(new FileWriter(fWrite, in_bAppend));
			brFile.write(getXML(true));
			brFile.close();
		}
	}
	/**
	 * Запись данного класса на диск в указанный файл.
	 * Открывает файл и записывает.
	 */
	public void WriteNew(String in_sFileNAme) throws IOException
	{
		if(in_sFileNAme != null)
		{
			File fWrite = new File(in_sFileNAme);
			BufferedWriter brFile;
			brFile = new BufferedWriter(new FileWriter(fWrite, false));
			brFile.write(getXML(true));
			brFile.close();
		}
	}

	/**
	 * Запись себя в открытый файл записи. После окончания записи делается FLUSH!!!
	 * @param in_bwLog
	 * @throws IOException
	 */
	public void Write(BufferedWriter in_bwLog) throws IOException
	{
		if(in_bwLog != null)
		{
			in_bwLog.write(getXML(true));
			in_bwLog.flush();
		}
	}
	/**
	 * Запись себя (формат команды IVC) в открытый файл записи. После окончания записи делается FLUSH!!!
	 * @param in_bwLog
	 * @throws IOException
	 */
	public void WriteIVC(BufferedWriter in_bwLog) throws Exception
	{
		if(in_bwLog != null)
		{
			String sBuf = getCommandIVC();
			sBuf = sBuf.replace("\n", "");
			in_bwLog.write(sBuf + "\n");
			in_bwLog.flush();
		}
	}

	public Vector<typeOb> getValList(String in_sKey)
	{
		Vector<typeOb> nodeRet = new Vector<typeOb>();
		xNode2<typeOb> node = getNodeForAdr(in_sKey, false);
		if(node != null)
		{
			for(xNode2<typeOb> xn : node.getParent())
			{
				if(xn.getKey().compareTo(node.getKey()) == 0)
				{
					nodeRet.add(xn.getVal());
				}
			}
		}
		return nodeRet;
	}

	/**
	 * Получить все узлы попадающие под данный ключ
	 * @param in_sKey ключ по которому искать
	 * @return список в качестве списка тот же узел, чтобы не парить
	 */
	public ArrayList<xNode2<typeOb>> getNodeList(String in_sKey)
	{
		xNode2<typeOb> nodeRet = new xNode2<typeOb>("ret");
		xNode2<typeOb> node = getNodeForAdr(in_sKey, false);
		if(node != null)
		{
			for(xNode2<typeOb> xn : node.getParent())
			{
				if(xn.getKey().compareTo(node.getKey()) == 0)
				{
					nodeRet.add(xn);
				}
			}
		}
		return nodeRet;
	}

	public xNode2<typeOb> getFirstNode()
	{
		return(get(0));
	}

	public void setParent(xNode2<typeOb> parent)
	{
		this.m_parentNode = parent;
	}

	// ru.artelecom.aus.DanilovAF.ats.atsProtokol
	public static void main(String[] args) throws Exception
	{
		xNode2 n = new xNode2<String>("один"); //.setVal("Один");
		xNode2 n1 = new xNode2<String>("два"); //.setVal("Два");
		xNode2 n3 = new xNode2<String>("три").setVal("Три");
		xNode2 n4 = new xNode2<String>("четыре").setVal("четыре");
		xNode2 n5 = new xNode2<String>("пять").setVal("пять");
		n1.add(n3);
		n1.add(n4);
		n1.add(n5);

		n.add(n1);
		System.out.println(n.getXML(true));
		n.del("два,четыре");
		System.out.println(n.getXML(true));
	}

	/**
	 * Найти первый узел с указанным названием
	 * Приоритет по текущему уровню, затем вглубь
	 * @param form - то, что ищем...
	 * @return
	 */
	public xNode2<typeOb> findeFirstToken(String form)
	{
		xNode2<typeOb> xRet = null;
		xNode2<typeOb> xCurrent = this;

		if(getKey().compareTo(form) == 0)
		{	// Нашли элемент первый
			xRet = this;
		}
		if(xRet == null)
		{
			for(xNode2<typeOb> xLevel: xCurrent)
			{
				xRet = xLevel.findeFirstToken(form);
				if(xRet != null)
				{
					break;
				}
			}
		}
		return(xRet);
	}

	/**
	 * Найти все узлы у которых имя токена равно input
	 * В следующем xml при поиске "key" будет найдено два узла...
	 * <xml>
	 *     <key>Bla - bla</key>
	 *     <prop>Tri - Bla
	 *     		<key>Bla - bla</key>
	 *     </prop>
	 * </xml>
	 * @param sNameNode - имя токена
	 * @return
	 */
	public ArrayList<xNode2<typeOb>> findeAllToken(String sNameNode)
	{
		ArrayList<xNode2<typeOb>> alRet = new ArrayList<xNode2<typeOb>>();
		return findeAllTokens(sNameNode, alRet);
	}

	/**
	 * Рекурсивная функция поиска узлов, с добавленным списком возврата
	 * @param sNameNode
	 * @param alRet
	 * @return
	 */
	public ArrayList<xNode2<typeOb>> findeAllTokens(String sNameNode, ArrayList<xNode2<typeOb>> alRet)
	{
//		System.out.println("------------>" + getKey());
		if(getKey().compareTo(sNameNode) == 0)
		{	// Нашли элемент первый
			alRet.add(this);
		}
		for(xNode2<typeOb> xLevel: this)
		{
			xLevel.findeAllTokens(sNameNode, alRet);
		}
		return alRet;
	}

//	public xNode2<typeOb> findeFirstProp(typeOb sVal)
//	{
//		xNode2<typeOb> xRet = null;
//		xNode2<typeOb> xCurrent = this;
//		if(getVal().compareTo(sVal) == 0)
//		{	// Нашли элемент первый
//			xRet = this;
//		}
//		if(xRet == null)
//		{
//			for(xNode2<typeOb> xLevel: xCurrent)
//			{
//				xRet = xLevel.findeFirstProp(sVal);
//				if(xRet != null)
//				{
//					break;
//				}
//			}
//		}
//		return(xRet);
//	}

//	public xNode2 indexOfProp(String sVal)
//	{
//
//		xNode2 xRet = null;
//		xNode2 xCurrent = this;
//		if(getVal().indexOf(sVal) != -1)
//		{	// Нашли элемент первый
//			xRet = this;
//		}
//		if(xRet == null)
//		{
//			for(xNode2 xLevel: xCurrent)
//			{
//				xRet = xLevel.indexOfProp(sVal);
//				if(xRet != null)
//				{
//					break;
//				}
//			}
//		}
//		return(xRet);
//	}

	@Override
	public String toString()
	{
		return (getKey() + " - " + getVal().toString());
	}
}


