package ru.DanilovAF.util.Json;

import ru.DanilovAF.util.MyException;
import ru.DanilovAF.util.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by aleksandr.danilov on 17.11.2017.
 * Узел для описания знаяения JSON
 */
public class JsonN
{
	JsonN parent = null;		// Родитель
	public Object val = null;				// Значение

	int iType = TYPE_NULL;
	static public int TYPE_NULL = 0;
	static public int TYPE_DIM = 1;
	static public int TYPE_STRING = 2;
	static public int TYPE_OBJECT = 4;
	static public int TYPE_INT = 8;

	static public int OTSTUP_COUNT = 2;
	private boolean empty;

	/**
	 * Добавление объекта по ключу
	 * @param valStr
	 * @param jVal
	 */
	public JsonN(String valStr, JsonN jVal) throws MyException {
		p(valStr, jVal);
//		iType = TYPE_STRING;
	}

	public JsonN(String valStr)
	{
		if(valStr == null)
		{
			iType = TYPE_NULL;
		} else
		{
			iType = TYPE_STRING;
		}
		this.val = valStr;
	}

	public JsonN()
	{

	}

	public JsonN(int iVal)
	{
		this.val = new Integer(iVal);
		iType = TYPE_INT;
	}

	public JsonN(double dVal)
	{
		this.val = new Double(dVal);
		iType = TYPE_INT;
	}

	public JsonN(String sKey, String sVal) throws MyException {
		p(sKey, new JsonN(sVal));
	}

	/**
	 * 1 - массив
	 * 0 - Пусто null
	 * 2 - Строка
	 * 4 - Объект
	 * 8 - Integer
	 * @return
	 */
	public int getValType()
	{
		return (iType);
	}

	private void toStringJson(StringBuffer sbB, int iLevel)
	{
		String sCh = sbB.substring(sbB.length() - 2, sbB.length() - 1);
		String sSpace = "";

		if(iLevel > 0)
		{
			sSpace = space(iLevel);
		}
		if(iType == TYPE_NULL)
		{
			sbB.append(sSpace).append("null,\n");
		} else if (val == null)
		{
		} else if (iType == TYPE_OBJECT)
		{
			for(String sKey : ((HashMap<String, JsonN>) val).keySet())
			{
				sbB.append(sSpace).append("\"").append(sKey).append("\": ");
				JsonN n = ((HashMap<String, JsonN>) val).get(sKey);
				n.toStringJsonObj(sbB, iLevel);
			}
		} else if(iType == TYPE_DIM)
		{
			for(JsonN n : (ArrayList<JsonN>) val)
			{
				n.toStringJsonObj(sbB, iLevel);
			}
			deleteComa(sbB);
		} else if(iType == TYPE_STRING)
		{
			appendSpace(sSpace, sbB).append("\"").append((String) val).append("\",\n");
		} else if(iType == TYPE_INT)
		{
			appendSpace(sSpace, sbB).append(val.toString()).append(",\n");
		} else if(iType == TYPE_NULL)
		{
			int y = 0;
		}
	}

	private StringBuffer appendSpace(String sSpace, StringBuffer sbB)
	{
		if(sbB.substring(sbB.length() - 1, sbB.length()).compareTo("\n") == 0)
		{
			sbB.append(sSpace);
		} else
		{

		}
		return(sbB);
	}

	private void toStringJsonObj(StringBuffer sbB, int iLevel)
	{
		String sSpace = "";
		if(iLevel > 0)
		{
			sSpace = space(iLevel);
		}
		int iBuf = getValType();
		if(iBuf == TYPE_OBJECT)
		{
			appendSpace(sSpace, sbB).append("{\n");
			toStringJson(sbB, iLevel + 1);
			deleteComa(sbB);
			sbB.append(sSpace).append("},\n");
		} else if(iBuf == TYPE_DIM)
		{
			if(val == null)
			{
				appendSpace(sSpace, sbB).append("[],\n");
			} else
			{
				appendSpace(sSpace, sbB).append("[\n");
				toStringJson(sbB, iLevel + 1);
				deleteComa(sbB);
				sbB.append(sSpace).append("],\n");
			}
		} else if(iBuf == TYPE_NULL || iBuf == TYPE_STRING || iBuf == TYPE_INT)
		{
			toStringJson(sbB, iLevel);
		}
	}

	private void deleteComa(StringBuffer sbB)
	{
		if(sbB.substring(sbB.length()-2, sbB.length()-1).compareTo(",") == 0)
		{
			sbB.delete(sbB.length() - 2, sbB.length() - 1);	//	Убрать запятую
		}
	}

	public String toStringJson(boolean flagFormat)
	{
		// Вывести от текущего узла
		JsonN node = this;
		int in_intLevel = 0;
		StringBuffer sRetB = new StringBuffer();
		sRetB.append("{\n");
		if(flagFormat)
		{
			toStringJson(sRetB, 2);
		} else
		{
			toStringJson(sRetB, 0);
		}
		deleteComa(sRetB);
		sRetB.append("}\n");
		if(!flagFormat)
		{
			util.trim(util.replace(sRetB, "\n", ""));
		}
		return(sRetB.toString());

	}

	public JsonN p(String sKey, String sVal) throws MyException
	{
		p(sKey, new JsonN(sVal));
		return(this);
	}

	public JsonN p(String sKey, JsonN nVal) throws MyException
	{
		if(getValType() == 4 || getValType() == 0)
		{
			if (val == null)
			{
				val = new HashMap<String, JsonN>();
				iType = 4;
			}
		} else
		{
			throw new MyException("Данный узел\n"+ toStringJson(true) + "не является объектом, его тип " + getValType() + ".", 0, MyException.LEV_NO_FATAL);
		}
		nVal.setParent(this);
		((HashMap<String, JsonN>) val).put(sKey, nVal);
		return (this);
	}

	public JsonN p(JsonN nVal) throws MyException
	{
		if(getValType() == 4 || getValType() == 0)
		{
			if (val == null)
			{
				val = new HashMap<String, JsonN>();
				iType = 4;
			}
		} else
		{
			throw new MyException("Данный узел\n"+ toStringJson(true) + "не является объектом, его тип " + getValType() + ".", 0, MyException.LEV_NO_FATAL);
		}
		if(nVal != null && nVal.getMap() != null) {
			for (String sKey : nVal.getMap().keySet()) {
//				p(sKey, nVal.getVal(sKey));
				p(sKey, nVal.get(sKey));
			}
		} else
		{
			if(nVal == null)
				throw new MyException("Добавляемый узел является null.", 0, MyException.LEV_NO_FATAL);
			throw new MyException("Добавляемый узел\n"+ nVal.toStringJson(true) + "не является объектом, его тип " + getValType() + ".", 0, MyException.LEV_NO_FATAL);
		}
		return (this);
	}

	public JsonN a(String sVal) throws MyException
	{
		a(new JsonN(sVal).setParent(this));
		return(this);
	}

	public JsonN a(String sKey, String iVal) throws MyException
	{
		return a(new JsonN().p(sKey, iVal));
	}

	public JsonN a(String sKey, int iVal) throws MyException
	{
		return a(new JsonN().p(sKey, iVal));
	}

	public JsonN a(String sKey, JsonN iVal) throws MyException
	{
		return a(new JsonN().p(sKey, iVal));
	}

	public JsonN a(JsonN nVal) throws MyException
	{
		if(getValType() == TYPE_DIM || getValType() == TYPE_NULL)
		{
			if (val == null)
			{
				val = new ArrayList<JsonN>();
				iType = 1;
			}
			nVal.setParent(this);
			((ArrayList<JsonN>) val).add(nVal);
		} else
		{
			throw new MyException("Данный узел\n"+ toStringJson(true) + "не является массивом, его тип " + getValType() + ".", 0, MyException.LEV_NO_FATAL);
		}
		return(this);
	}

	public static void main(String[] args) throws MyException
	{
//		JsonN node = new JsonN().p("auth", (String) null).p("id", 2).p("jsonrpc", "2.0").p("method", "host.get").p("params", new JsonN().p("output", new JsonN().a("hostid").a("testObjDim", new JsonN().a("one").a("two")).a("host")).p("selectInterfaces", new JsonN().a("interfaceid").a("ip")));
//		JsonN node = new JsonN().p("auth", "0424bd59b807674191e7d77572075f33").p("id", 2).p("jsonrpc", "2.0").p("method", "host.get").p("params", new JsonN().p("output", new JsonN().a(new JsonN().a("hostid").a("IDD")).a("host")).p("selectInterfaces", new JsonN().a("interfaceid").a("ip")));
//		String sBuf = node.toStringJson(true);
//		StringBuffer sb = new StringBuffer("{\"id\": 2,\"method\": \"host.get\",\"params\": {\"output\": \"extend\",\"filter\": { \"host\": [ \"a130\" ]}},\"jsonrpc\": \"2.0\",\"auth\": \"08f9eb6c9487849c25a8528de6435759\"}");
//		JsonN node = new JsonN().p("jsonrpc", "2.0").p("method", "template.get").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a("templateid").a("name")).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String)null);
//		JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name").a(new JsonN().setiType(TYPE_DIM))).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
		JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name").a(new JsonN().setiType(TYPE_DIM))).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
//		JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name")).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
		System.out.println(node.toStringJson(true));
//		StringBuffer sb = new StringBuffer(node.toStringJson(true));
		StringBuffer sb = new StringBuffer("{\n" +
				"  \"id\": 10\n" +
				"}\n");
//		StringBuffer sb = new StringBuffer("{\n" +
//				"  \"id\": 10,\n" +
//				"  \"params\": {\n" +
//				"    \"selectDiscoveries\": \"extend\",\n" +
//				"    \"output\": [\n" +
//				"      {\n" +
//				"        \"nullDim\": [],\n" +
//				"        \"templateid\": 11\n" +
//				"      },\n" +
//				"      [],\n" +
//				"      null,\n" +
//				"      22,\n" +
//				"      {\n" +
//				"        \"name\": \"noname\"\n" +
//				"      }\n" +
//				"    ],\n" +
//				"    \"filter\": {\n" +
//				"      \"host\": [\n" +
//				"        \"Cisco CDP Neibors\"\n" +
//				"      ]\n" +
//				"    }\n" +
//				"  },\n" +
//				"  \"jsonrpc\": \"2.0\",\n" +
//				"  \"auth\": null,\n" +
//				"  \"jsonrpc\": \"3.0\"\n" +
//				"}\n");
		System.out.println(sb);

		JsonN nn = JsonN.parse(sb, 0);

//		nn.get("params,filter,host", false).a("Cisco LLDP Neibors");

		JsonN n1 = nn.getN("params,output,nullDim").p("newKey", "newVal");
		System.out.println(nn.toStringJson(true));
		JsonN n2 = n1.getParent();
		n1.p("dim", new JsonN().a("empty1").a("empty2").a("empty3"));
		n2 = n1.g("dim");
		n2.a("add1").a("add2").a("add3");
		n2.p("key1", "Val1").p("key2", "Val2");

		nn.g("params,output").p("newFim2", new JsonN().a(new JsonN().p("val", "ValNew")));
		System.out.println(nn.toStringJson(true));
	}

	public JsonN g(String sAddr) throws MyException
	{
		return get(sAddr, false);
	}

	public JsonN getN(String sAddr) throws MyException
	{
		return get(sAddr, true);
	}

	public JsonN remove(String sAddr) throws MyException
	{
		JsonN nodeRet = this;
		JsonN nodeDel = getNull(sAddr);
		if(nodeDel != null) {
			JsonN nodeDelparent = nodeDel.getParent();
			if(nodeDelparent.iType == TYPE_OBJECT)
			{
				String sKey = util.field(sAddr, ",", -1);
				nodeDelparent.getMap().remove(sKey);
			} else if(nodeDelparent.iType == TYPE_DIM) {

			}
		}
		return nodeRet;
	}

	/**
	 * Поиск узла по пути без выбрасывания исключения
	 * @param sAddr
	 * @return
	 */
	public JsonN getNull(String sAddr)
	{
		JsonN nodeRet = this;
		if(sAddr != null && !sAddr.isEmpty()) {
			for (String sKey : sAddr.split(",")) {
				String sIndex = util.field(sKey, "[", 2);
				int index = -2;
				if(!sIndex.isEmpty()) {
					sKey = util.field(sKey, "[", 1);
					sIndex = util.field(sIndex, "]", 1);
					index = (int) util.mcn(sIndex, '.');
				}
				if (nodeRet.getValType() == TYPE_OBJECT) {
					if (((HashMap<String, JsonN>) nodeRet.val).containsKey(sKey)) {
						nodeRet = ((HashMap<String, JsonN>) nodeRet.val).get(sKey);    // Заменим текущий узел
					} else {
						nodeRet = null;
						break;
					}
				} else if (nodeRet.getValType() == TYPE_NULL) {
					nodeRet = null;
					break;
				} else if (nodeRet.getValType() == TYPE_DIM) {
					boolean bFinde = false;
					if (nodeRet.val != null) {
						for (JsonN n : ((ArrayList<JsonN>) nodeRet.val)) {
							if (n.getValType() == TYPE_OBJECT) {
								if (((HashMap<String, JsonN>) n.val).containsKey(sKey)) {
									nodeRet = ((HashMap<String, JsonN>) n.val).get(sKey);    // Заменим текущий узел
									bFinde = true;
									break;
								} else {    // А ничего не делаем, мы же в массиве узлов - надо пройти все, чтобы принять решение что с этим делать
								}
							} else {    // А ничего не делаем , т.к. может быть в других элементах массива найдем
							}
						}
					}
					if (!bFinde) {    // не нашли в массиве что будем делать?
						nodeRet = null;
						break;
					}
				}
				if(index != -2 && nodeRet != null)
				{   // Еще надо выделить номер из массива
					if (nodeRet.getValType() != TYPE_DIM) {
						nodeRet = null;
						break;
					}
					if(nodeRet.getDim() == null || nodeRet.getDim().size() <= index) {
						nodeRet = null;
						break;
					}
					if(index == -1)
						index = nodeRet.getDim().size() - 1;
					nodeRet = nodeRet.getDim().get(index);
				}
			}
		}
		return nodeRet;
	}
	/**
	 * Адрес состоит из ключей, если ищем в HashMap - тогда по ключам объектов получаем нужный узел
	 * Если на пути встречается массив, то идем по элементам массива и ищем внутри или указанное значение, или если внутри массива мапа - ищем по ключу в этом мапе
	 * @param sAddr
	 * @return
	 */
	public JsonN get(String sAddr, boolean flagCreate) throws MyException
	{
		JsonN nodeRet = this;
		for(String sKey: sAddr.split(","))
		{
			if (nodeRet.getValType() == TYPE_OBJECT)
			{
				if(((HashMap<String, JsonN>)nodeRet.val).containsKey(sKey))
				{
					nodeRet = ((HashMap<String, JsonN>)nodeRet.val).get(sKey);	// Заменим текущий узел
				} else
				{
					if(flagCreate)	// Если надо создавать отсутствующие данные
					{
						nodeRet.p(sKey, new JsonN());
						nodeRet = ((HashMap<String, JsonN>)nodeRet.val).get(sKey);	// Заменим текущий узел
					} else
					{	// Если не создавать - то вернем нулл
//						nodeRet = null;
//						break;
						throw new MyException("Нет ключа " + sKey + " в пути " + sAddr + ".", 0, MyException.LEV_NO_FATAL);
					}
				}
			} else if(nodeRet.getValType() == TYPE_NULL)
			{
				if(flagCreate)	// Если надо создавать отсутствующие данные
				{
					nodeRet.p(sKey, new JsonN());
					nodeRet = ((HashMap<String, JsonN>)nodeRet.val).get(sKey);	// Заменим текущий узел
				} else
				{	// Если не создавать - то вернем нулл
//					nodeRet = null;
//					break;
					throw new MyException("Нет ключа " + sKey + " в пути " + sAddr + ".", 0, MyException.LEV_NO_FATAL);
				}
			} else if(nodeRet.getValType() == TYPE_DIM)
			{
				boolean bFinde = false;
				if(nodeRet.val != null) {
					for (JsonN n : ((ArrayList<JsonN>) nodeRet.val)) {
						if (n.getValType() == TYPE_OBJECT) {
							if (((HashMap<String, JsonN>) n.val).containsKey(sKey)) {
								nodeRet = ((HashMap<String, JsonN>) n.val).get(sKey);    // Заменим текущий узел
								bFinde = true;
								break;
							} else {    // А ничего не делаем, мы же в массиве узлов - надо пройти все, чтобы принять решение что с этим делать
							}
						} else {    // А ничего не делаем , т.к. может быть в других элементах массива найдем
						}
					}
				}
				if(!bFinde)
				{	// не нашли в массиве что будем делать?
					int y = 0;
					if(flagCreate)
					{
						JsonN nBuf = new JsonN().p(sKey, new JsonN());
						nodeRet.a(nBuf);
						nodeRet = nBuf;
					} else
					{
//						nodeRet = null;
//						break;
						throw new MyException("Нет ключа " + sKey + " в пути " + sAddr + ".", 0, MyException.LEV_NO_FATAL);
					}
				}
			}
		}
		return nodeRet;
	}

	public JsonN p(String sKey, int iVal) throws MyException
	{
		p(sKey, new JsonN(iVal));
		return(this);
	}

	private JsonN p(String sKey, double dVal) throws MyException
	{
		p(sKey, new JsonN(dVal));
		return(this);
	}

	public String space(int in_intCount)
	{
		String sBuf = "";
		while(--in_intCount != 0 && in_intCount > 0)
		{
			sBuf += "   ";
		}
		return(sBuf);
	}

	public JsonN setParent(JsonN parent)
	{
		this.parent = parent;
		return this;
	}
	public JsonN getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return toStringJson(false);
	}

	public static int STAT_BEGIN = 0;
	public static int STAT_OBJ = 4;
	public static int STAT_IN_QUOTA = 16;

	public static JsonN parse(StringBuffer sb, int delta) throws MyException
	{
//		JsonN nRet = new JsonN();
		JsonN nRet = null;
		JsonN curNode = nRet;
		int iStat = 0;
		StringBuffer sbStack = new StringBuffer();    // Обратный стек в первом значении текущий элемент
		StringBuffer sbVal = new StringBuffer();
		ArrayList<String> stakKey = new ArrayList<String>();	// Стек для ключей
		ArrayList<JsonN> stakNode = new ArrayList<JsonN>();	// Стек для узлов

		String sKey = "";
		for(int i = delta; i < sb.length(); i++)
		{
			char chStat = 0;
			if (sbStack.length() != 0)
			{
				chStat = sbStack.charAt(0);
			}
			char ch = sb.charAt(i);
			if (ch == '{' && chStat != '"')
			{    // Вход в новый объект
				sbStack.insert(0, "{");
				stakNode.add(0, curNode);
				curNode = new JsonN().setiType(TYPE_OBJECT);
			} else if (ch == '\\' && chStat == '"')
			{	// Маскирование следующего символа
				if (chStat == '\\')
				{
					sbVal.append(ch);
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли
				} else
				{
					sbStack.insert(0, '/');
				}
			} else if (ch == '"')
			{
				if (chStat == '/')
				{    // Маскирование кавычки
					sbVal.append(ch);
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли
				} else if (chStat != '"')
				{    // Начало нового данного
					sbStack.insert(0, '"');
					sbVal.delete(0, sbVal.length());    // Почистим буфер данных
				} else
				{    // Закрытие кавычки, все зависит от того где мы находимся
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли
					// Добавление в стек, что только - что нашли строку
					sbStack.insert(0, 's');
					sKey = sbVal.toString();        // Присвоим ключ
					stakKey.add(0, sKey);		// Добавили ключ в стек
					sbVal.delete(0, sbVal.length());    // Почистим буфер данных
				}
			} else if ((ch == 'u' || ch == 'r' || ch == 'n') && chStat == '/')
			{	// Следом должно идти 4-х значное шестнадчатеричное
				sbVal.append("\\").append(ch);
				sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли
			} else if (ch == '[' && chStat != '"')
			{    // Вход в массив данных
				// Проверим валидность, сюда можно зайти не всегда
				if(chStat == ':' || chStat == '[')
				{	// Такого быть не должно
					sbVal.delete(0, sbVal.length());    // Почистим буфер данных
					sbStack.insert(0, "[");
					stakNode.add(0, curNode);
					curNode = new JsonN().setiType(TYPE_DIM);
				} else
				{
					int iLen = 30;
					if(i < 30) { iLen = i; }
					throw new MyException("Здесь не может быть символа <" + sb.substring(i - iLen, i) + "> :", 0, MyException.LEV_FATAL);
				}
			} else if (ch == ':' && chStat != '"')
			{    // Вход в значение по ключу
				// Проверим валидность, сюда можно зайти не всегда
//				if (sbStack.indexOf("s{") == 0 || sbStack.indexOf("s[") == 0)
				if (sbStack.indexOf("s{") == 0)
				{    // Значит мы нашли ключ
					sbVal.delete(0, sbVal.length());    // Почистим буфер данных
					sbStack.insert(0, ":");
				} else
				{	// Вырезать кусок буфера для анализа
					int iLen = 30;
					if(i < 30) { iLen = i; }
					throw new MyException("Здесь не может быть символа <" + sb.substring(i - iLen, i) + "> <" + sbStack + ">:", 0, MyException.LEV_FATAL);
				}
			} else if (ch == ',' && chStat != '"')
			{    // Разделитель между данными
				if (sbStack.indexOf("s:s{") == 0)
				{	// Значение по ключу
					String sBuf = stakKey.get(0); stakKey.remove(0);
					sKey = stakKey.get(0); stakKey.remove(0);
					curNode.p(sKey, sBuf);
					sKey = null;
					sbStack.delete(0, 3);    // Удалим из стека операцию, которую только что закрыли :
				} else if (sbStack.indexOf("d:s") == 0)
				{	// Вставить по ключу массив
					sKey = stakKey.get(0); stakKey.remove(0);

					stakNode.get(0).p(sKey, curNode);	// Присвоим стековому узлу текущий узел по стековому ключу
					curNode = stakNode.get(0);		// Удалим из стека узлов одну позицию
					stakNode.remove(0);			// Убрали из стека узел, т.к. закрыли скобу
					sKey = null;
					sbStack.delete(0, 3);    // Удалим из стека операцию, которую только что закрыли :
				} else if (sbStack.indexOf("o:s") == 0)
				{	// Вставить по ключу массив
					sKey = stakKey.get(0); stakKey.remove(0);

					stakNode.get(0).p(sKey, curNode);	// Присвоим стековому узлу текущий узел по стековому ключу
					curNode = stakNode.get(0);		// Удалим из стека узлов одну позицию
					stakNode.remove(0);			// Убрали из стека узел, т.к. закрыли скобу
					sKey = null;
					sbStack.delete(0, 3);    // Удалим из стека операцию, которую только что закрыли :
				} else if (sbStack.indexOf(":s") == 0)
				{	// Здесь должно быть значение	// проверить на null
					String sBuf = util.trimLR(sbVal, " \t\n").toString();
					if(sBuf.isEmpty())
					{	// Такого быть не должно - это ошибка
						int iLen = 30;
						if(i < 30) { iLen = i; }
						throw new MyException("Здесь не может быть символа <" + sb.substring(i - iLen, i) + "> <" + sbStack + ">:", 0, MyException.LEV_FATAL);
					}
					sKey = stakKey.get(0); stakKey.remove(0);
					putVal(curNode, sKey, sBuf);
					sKey = null;
					sbStack.delete(0, 2);    // Удалим из стека операцию, которую только что закрыли :
				} else if (sbStack.indexOf("s[") == 0)
				{	// Добавить в массив текущий узел
					String sBuf = stakKey.get(0); stakKey.remove(0);
					curNode.a(sBuf);
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли :
				} else if (sbStack.indexOf("o[") == 0 || sbStack.indexOf("d[") == 0)
				{	// Добавить в массив текущий объект
					stakNode.get(0).a(curNode);	// Присвоим стековому узлу текущий узел по стековому ключу
					curNode = stakNode.get(0);		// Удалим из стека узлов одну позицию
					stakNode.remove(0);			// Убрали из стека узел, т.к. закрыли скобу
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли :
				} else if (chStat == '[')
				{	// Здесь должно быть значение	// проверить на null
					String sBuf = util.trimLR(sbVal, " \t\n").toString();
					if(sBuf.isEmpty())
					{	// Такого быть не должно - это ошибка
						int iLen = 30;
						if(i < 30) { iLen = i; }
						throw new MyException("Здесь не может быть символа <" + sb.substring(i - iLen, i) + "> <" + sbStack + ">:", 0, MyException.LEV_FATAL);
					}
					addVal(curNode, sBuf);
				} else
				{	// Вырезать кусок буфера для анализа
					int iLen = 30;
					if(i < 30) { iLen = i; }
					throw new MyException("Здесь не может быть символа <" + sb.substring(i - iLen, i) + "> <" + sbStack + ">:", 0, MyException.LEV_FATAL);
				}
				sbVal.delete(0, sbVal.length());    // Почистим буфер данных
			} else if (ch == '}' && chStat != '"')
			{    // Выход из Объекта
				// Проверим возможность этого тэга !!!
				// Если что-то есть в sbVal - это может быть данными - числом или нулём
				if (sbStack.indexOf("d:s{") == 0)
				{    // Вставить по ключу массив
//					if (stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
					sKey = stakKey.get(0);
					stakKey.remove(0);

					stakNode.get(0).p(sKey, curNode);    // Присвоим стековому узлу текущий узел по стековому ключу
					curNode = stakNode.get(0);        // Удалим из стека узлов одну позицию
					stakNode.remove(0);            // Убрали из стека узел, т.к. закрыли скобу
					sbStack.delete(0, 4);    // Удалим из стека операцию, которую только что закрыли :
					if(sbStack.length() != 0) { sbStack.insert(0, "o"); }
				} else if (sbStack.indexOf("o:s{") == 0)
				{    // Вставить по ключу массив
//					if (stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
					sKey = stakKey.get(0);
					stakKey.remove(0);

					stakNode.get(0).p(sKey, curNode);    // Присвоим стековому узлу текущий узел по стековому ключу
					curNode = stakNode.get(0);        // Удалим из стека узлов одну позицию
					stakNode.remove(0);            // Убрали из стека узел, т.к. закрыли скобу
					sbStack.delete(0, 4);    // Удалим из стека операцию, которую только что закрыли :
					if (sbStack.length() != 0)
					{
						sbStack.insert(0, "o");
					}
				} else if (sbStack.indexOf("s:s{") == 0)
				{	// Значение по ключу
//					if(stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
					String sBuf = stakKey.get(0); stakKey.remove(0);
					sKey = stakKey.get(0); stakKey.remove(0);
					curNode.p(sKey, sBuf);
					sKey = null;
					sbStack.delete(0, 4);    // Удалим из стека операцию, которую только что закрыли :
					if (sbStack.length() != 0)
					{
						sbStack.insert(0, "o");
					}
				} else if (sbStack.indexOf(":s{") == 0)
				{	// Здесь должно быть значение	// проверить на null
					String sBuf = util.trimLR(sbVal, " \t\n").toString();
//					if(stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
					sKey = stakKey.get(0); stakKey.remove(0);
					putVal(curNode, sKey, sBuf);
					sbStack.delete(0, 3);    // Удалим из стека операцию, которую только что закрыли :
					if(sbStack.length() != 0) { sbStack.insert(0, "o"); }
				} else if (chStat == '{')
				{
					chStat = sbStack.length() > 0 ?  sbStack.charAt(0) : 0;
					if(chStat == ':')
					{	// Ключу присвоить текущий объект
						if(stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
						sKey = stakKey.get(0); stakKey.remove(0);
						stakNode.get(0).p(sKey, curNode);	// Присвоим стековому узлу текущий узел по стековому ключу
						curNode = stakNode.get(0);		// Удалим из стека узлов одну позицию
						stakNode.remove(0);			// Убрали из стека узел, т.к. закрыли скобу
						sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли :
					} else
					{	// Пока не знаем что делать
						if (sbStack.length() != 0)
						{
							sbStack.setCharAt(0, 'o');
						}
					}
				} else if (chStat == '[')
				{
					int y = 0;
				}
				sbVal.delete(0, sbVal.length());    // Почистим буфер данных
//				curNode = curNode.getParent();
			} else if (ch == ']' && chStat != '"')
			{    // Выход из массива данных
				if (sbStack.indexOf("s[") == 0)
				{	// Надо добавить строку и закрыть массив
//					if(stakKey.isEmpty()) { throw new MyException("Стек ключей пуст!!!", 0 , MyException.LEV_FATAL); }
					sKey = stakKey.get(0); stakKey.remove(0);	// Вынули из стека строку
					curNode.a(sKey);
					sbStack.delete(0, 2);    // Удалим из стека операцию, которую только что закрыли
					sbStack.insert(0, "d");	// Добавили, что закрыли массив и он в стеке в виде текущего узла
				} else if (sbStack.indexOf("d[") == 0 || sbStack.indexOf("o[") == 0)
				{	// Добавить в массив текущий узел
					stakNode.get(0).a(curNode);		// Добавим текущий массив
					curNode = stakNode.get(0);		// Удалим из стека узлов одну позицию
					stakNode.remove(0);			// Убрали из стека узел, т.к. закрыли скобу
					sbStack.delete(0, 2);    // Удалим из стека операцию, которую только что закрыли :
					sbStack.insert(0, "d");	// Добавили, что закрыли массив и он в стеке в виде текущего узла
				} else if(chStat == '[')
				{	// Закрытие массива пустого или в переменной val что-то есть
					String sBuf = util.trimLR(sbVal, " \t\n").toString();
					if(!sBuf.isEmpty())
					{	// Нажо в массив добавить значение не строковое
						addVal(curNode, sBuf);
					}
					sbStack.delete(0, 1);    // Удалим из стека операцию, которую только что закрыли
					sbStack.insert(0, "d");	// Добавили, что закрыли массив и он в стеке в виде текущего узла
				}
				sbVal.delete(0, sbVal.length());    // Почистим буфер данных
			} else
			{    // Просто копим буфер
				sbVal.append(ch);
				if(chStat == '/')
					sbStack.delete(0, 1);    // Удалим из стека т.к. мы только что добавили маскируемый символ
			}
		}

		return curNode;
	}

	private static void addVal(JsonN curNode, String sbuf) throws MyException
	{
		if(isNumber(sbuf) == 1)	// Целое
		{
			curNode.a(new JsonN((int) util.mcn(sbuf, '.')));
		} else if(isNumber(sbuf) == -1)	// doble
		{
			curNode.a(new JsonN(util.mcn(sbuf, '.')));
		} else if(sbuf.compareTo("null") == 0)	// NULL
		{
			curNode.a(new JsonN());
		} else
		{
			curNode.a(sbuf);
		}
	}

	private static void putVal(JsonN curNode, String sKey, String sbuf) throws MyException
	{
		if(isNumber(sbuf) == 1)	// Целое
		{
			curNode.p(sKey, (int) util.mcn(sbuf, '.'));
		} else if(isNumber(sbuf) == -1)	// doble
		{
			curNode.p(sKey, util.mcn(sbuf, '.'));
		} else if(sbuf.compareTo("null") == 0)	// NULL
		{
			curNode.p(sKey, new JsonN());
		} else
		{
			curNode.p(sKey, sbuf);
		}
	}

	private static int isNumber(String sbuf)
	{
		int iRet = 0;
		if(sbuf.indexOf('.') != -1)
		{
			if(sbuf.compareTo("" + util.mcn(sbuf, '.')) == 0)
			{
				iRet = -1;
			}
		} else if(sbuf.indexOf(',') != -1)
		{
			if(sbuf.compareTo("" + util.mcn(sbuf, ',')) == 0)
			{
				iRet = -1;
			}
		} else if(sbuf.compareTo("" + (int)util.mcn(sbuf, ',')) == 0)
		{
			iRet = 1;
		}
		return iRet;
	}

	public JsonN setiType(int iType)
	{
		this.iType = iType;
		val = null;
		return(this);
	}

	public boolean isEmpty()
	{
		boolean bRet = false;
		if(getValType() == TYPE_NULL)
			bRet = true;
		return bRet;
	}

	public JsonN get(String s) throws MyException
	{
		return get(s, false);
	}

	public ArrayList<JsonN> getDim()
	{
		ArrayList<JsonN> cRet = null;
		if(getValType() == TYPE_DIM)
		{
			if(val == null)
				val = new ArrayList<JsonN>();
			cRet = (ArrayList<JsonN>) val;
		} else
		{
			cRet = new ArrayList<JsonN>();
		}
		return(cRet);
	}

	public HashMap<String, JsonN> getMap()
	{
		HashMap<String, JsonN> cRet = null;
		if(getValType() == TYPE_OBJECT)
		{
			cRet = (HashMap<String, JsonN>) val;
		} else
		{
			cRet = new HashMap<String, JsonN>();
		}
		return(cRet);
	}
	public boolean containKey(String sKey) {
		boolean bRet = false;
		if(getValType() == TYPE_OBJECT)
		{
			if(((HashMap<String, JsonN>) val).containsKey(sKey)) {
				bRet = true;
			}
		}
		return(bRet);
	}

	public String getKey() throws MyException {
		String sRet = "";
		if(getValType() == TYPE_OBJECT)
		{
			if(((HashMap<String, JsonN>) val).size() == 1) {
				for (String sKey : ((HashMap<String, JsonN>) val).keySet()) {
					sRet = sKey;
				}
			} else
			{
				throw new MyException("Неверное извлечение значения из JSON " + toString(), 0, MyException.LEV_NO_FATAL);
			}
		} else
		{
			throw new MyException("Неверное извлечение значения из JSON " + toString(), 0, MyException.LEV_NO_FATAL);
		}
		return(sRet);
	}

	/**
	 * Возвращает значение, если значения нет или значение не строка и не цифра - возвращяет NULL
	 * @return
	 * @throws MyException
	 */
	public String getKeyNoTh() throws MyException {
		String sRet = null;
		if(getValType() == TYPE_OBJECT)
		{
			if(((HashMap<String, JsonN>) val).size() == 1) {
				for (String sKey : ((HashMap<String, JsonN>) val).keySet()) {
					sRet = sKey;
				}
			} else
			{
			}
		} else
		{
		}
		return(sRet);
	}

	public String getVal(String sKey) throws MyException {
		String sRet = null;
		if(getValType() == TYPE_OBJECT)
		{
			if(((HashMap<String, JsonN>) val).containsKey(sKey))
			{
				sRet = ((HashMap<String, JsonN>) val).get(sKey).getVal();
			}
		} else
		{
			throw new MyException("Неверное извлечение значения из JSON " + toString(), 0, MyException.LEV_NO_FATAL);
		}
		return(sRet);
	}

	/**
	 * Если неуспешное извлечение - возвращается null
	 * @param sKey
	 * @return
	 * @throws MyException
	 */
	public String getValNoTh(String sKey) throws MyException {
		String sRet = null;
		if(getValType() == TYPE_OBJECT)
		{
			if(((HashMap<String, JsonN>) val).containsKey(sKey))
			{
				sRet = ((HashMap<String, JsonN>) val).get(sKey).getVal();
			}
		} else
		{
		}
		return(sRet);
	}

	/**
	 * Может вернуть null т.к. значение в JSON может быть null
	 * @return
	 */
	public String getVal() throws MyException {
		String sRet = "";
		int iType = getValType();
		if(iType == TYPE_STRING || iType == TYPE_INT)
		{
			sRet = val.toString();
		} else if(iType == TYPE_NULL) {
			sRet = null;
		} else
		{
			throw new MyException("Неверное извлечение значения из JSON " + toString(), 0, MyException.LEV_NO_FATAL);
		}
		return(sRet);
	}

	/**
	 * Может вернуть null если не верный файл
	 * @param in_F
	 * @return
	 * @throws Exception
	 */
	static public JsonN inputFileUTF_0A(File in_F) throws Exception
	{
		if(in_F.isFile())
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(in_F.getCanonicalFile()),	"UTF8"));
			String sLine;
			StringBuffer sb = new StringBuffer();
			JsonN js = new JsonN();
			while((sLine = in.readLine()) != null)
			{
//				System.out.println(sLine);
				sb.append(sLine).append("\n");
			}
			in.close();
			return js.parse(sb, 0);
		}
		return null;
	}

}

















