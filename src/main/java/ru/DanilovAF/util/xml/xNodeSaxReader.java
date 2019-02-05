package ru.DanilovAF.util.xml;

import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 28.12.2005
 * Time: 8:43:42
 * To change this template use File | Settings | File Templates.
 */
public class xNodeSaxReader extends DefaultHandler2 {

	/**
	 * Корневой узел
	 */
	protected xNode m_RootNode;
	/**
	 * Текущий узел для обработки
	 */
	protected xNode m_CurrentNode;
	/**
	 * Флаг говорит о том какое состояние было шаг назад при поиске sax.
	 * 0 - characters
	 * -1 - endElement
	 * 1 - startElement
	 */
	protected int m_intFlagElement;

	/**
	 *  Флаг говорит, что закончился один документ и он находится в m_NodeDone, его оттуда можно взять и поюзать
	 */
	protected int flagDone = 0;
	protected xNode m_NodeDone;
	protected XMLReader m_sax;

	public xNode getM_NodeDone()
	{
		return m_NodeDone;
	}

	public int getFlagDone()
	{
		return flagDone;
	}

	public void setFlagDone(int flagDone)
	{
		this.flagDone = flagDone;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(m_CurrentNode.getParent() != null)
		{
			m_CurrentNode = m_CurrentNode.getParent();
		} else
		{
			// Завершение работы
		}
		m_intFlagElement = -1;
		//System.out.println(localName);
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if(m_CurrentNode == null)
		{
			m_CurrentNode = m_RootNode; // сделали его текущим
		} else
		{
			//xNode nodeBuf = new xNode(m_CurrentNode);
			xNode nodeBuf = new xNode();
			m_CurrentNode.add(nodeBuf);
			m_CurrentNode = nodeBuf;
		}
		m_CurrentNode.setKey(localName);
		m_intFlagElement = 1;
		// Отработаем атрибуты
		for(int i = 0; i < attributes.getLength(); i++)
		{
			m_CurrentNode.setProp(attributes.getLocalName(i), attributes.getValue(i));
		}
		//System.out.println(localName);
	}

    public xNodeSaxReader()
    {
        super();
        m_RootNode = new xNode();   // Создали корень дерева
    }

    public void characters(char ch[], int start, int length) throws SAXException
	{
        //super.characters(ch, start, length);    //To change body of overridden methods use File | Settings | File Templates.
		if(m_intFlagElement == 0)
		{
			m_CurrentNode.setValAppend(new String(ch, start, length));
		} else
		{
			m_CurrentNode.setVal(new String(ch, start, length));
		}
		m_intFlagElement = 0;
		//System.out.println(new String(ch, start, length));
	}

    public xNode doXmlFile(String in_sFileName)
	{
		m_RootNode.RemoveAll();
		try
		{
			XMLReader sax = XMLReaderFactory.createXMLReader();
			InputSource is = new InputSource(new BufferedReader(new FileReader(new File(in_sFileName))));
			sax.setContentHandler(this);
			sax.parse(is);
		}
		catch(SAXException e)
        {
            System.err.println(e.getMessage());
        }
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}

		return(m_RootNode);
	}

	// Начало документа XML сюда передается первая строка затем пока не наступит событие endDocument перевать данные в строках
	// в функцию procedXML
	public void beginXML(String in_sXml)
	{
		m_RootNode.RemoveAll();
		try
		{
			m_sax = XMLReaderFactory.createXMLReader();
			InputSource is = new InputSource(new ByteArrayInputStream(in_sXml.getBytes()));
			m_sax.setContentHandler(this);
			m_sax.parse(is);
		}
		catch(SAXException e)
        {
            System.err.println(e.getMessage());
        }
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}

	}
	public void procedXML(String in_sXml)
	{
		InputSource is = new InputSource(new ByteArrayInputStream(in_sXml.getBytes()));
		try
		{
			m_sax.parse(is);
		} catch (IOException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (SAXException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Override
	public void endDocument() throws SAXException
	{
		super.endDocument();
		m_NodeDone = m_RootNode;
		m_RootNode.clear();
	}

	public xNode doXml(String in_sXml)
	{
		m_RootNode.RemoveAll();
		try
		{
			XMLReader sax = XMLReaderFactory.createXMLReader();
			InputSource is = new InputSource(new ByteArrayInputStream(in_sXml.getBytes()));
			sax.setContentHandler(this);
			sax.parse(is);
		}
		catch(SAXException e)
        {
            System.err.println(e.getMessage());
        }
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}

		return(m_RootNode);
	}

	public static void main(String[] args)
    {
        xNodeSaxReader xRead = new xNodeSaxReader();
        try
        {
            String sXml = "<block>\n" +
					"\t<get>\n" +
					"\t\t<atmt>\n" +
					"\t\t\t42\n" +
					"\t\t</atmt>\n" +
					"\t\t<filter>\n" +
					"\t\t</filter>\n" +
					"\t</get>\n" +
					"</block>";
            XMLReader sax = XMLReaderFactory.createXMLReader();
            try
            {
                InputSource is = new InputSource(new ByteArrayInputStream(sXml.getBytes()));
                sax.setContentHandler(xRead);
				sax.parse(is);
				String sBuf;
				//sBuf = xRead.m_RootNode.getXML(sBuf);
				//System.out.println(sBuf);
				sBuf = xRead.m_RootNode.getVal("get,ats1");
				System.out.println(sBuf == null ? "" : sBuf);

				xNode xN1;
				xN1 = new xNode("block");
				xN1.getNodeForAdr("get,atmt", true).setVal("42");
				xN1.getNodeForAdr("get,filter", true).setVal("Проба пера (фильтр)");
				sBuf = xN1.getXML(true);
				System.out.println(sBuf == null ? "" : sBuf);

			}
            catch(IOException e)
            {
                System.err.println(e.getMessage());
            }
        }
        catch(SAXException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
