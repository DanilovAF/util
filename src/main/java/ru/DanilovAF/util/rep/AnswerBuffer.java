package ru.DanilovAF.util.rep;

import ru.DanilovAF.util.M;
import ru.DanilovAF.util.data.ItemData;
import ru.DanilovAF.util.data.MyTableModel;
import ru.DanilovAF.util.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aleksandr.Danilov on 16.01.2020.
 *
 * Работа с ответом циски
 * Захочется спросить все ли команды успешны
 * Получить значение неуспешной команды и ее ошибку
 * Получить ответ по номеру команды. Если
 *
 */
public class AnswerBuffer {
	public StringBuilder val;

	public boolean isEmpry() {
		return false;
	}


	public AnswerBuffer(StringBuilder in_val) {
		this.val = in_val;
		initC();
	}

	public void initC() {
	}

	public AnswerBuffer(StringBuffer in_val) {
		if(in_val == null)
			in_val = new StringBuffer();
		if(val == null) {
			val = new StringBuilder(in_val);
		} else {
			val.delete(0, -1).append(in_val);
		}
		initC();
	}
	public AnswerBuffer(String in_val) {
		if(in_val == null)
			in_val = "";
		if(val == null) {
			val = new StringBuilder(in_val);
		} else {
			val.delete(0, -1).append(in_val);
		}
		initC();
	}

	// Найдет все позиции вхождения блока
	public ArrayList<PointTxt> getAllPart(String sBeg, String sEnd, PointTxt fromPos) {
		if (fromPos == null)
			fromPos = new PointTxt();
		ArrayList<PointTxt> alRet = new ArrayList<>();

		while ((fromPos = getPosBegStrToEndStr(sBeg, sEnd, fromPos)).x != -1) {
			alRet.add(new PointTxt(fromPos));
		}
		return alRet;
	}

	/**
	 * Получить начало следующей строки
	 * @param iPos
	 * @return
	 */
	public int getEndLinePos(int iPos) {
		int intEn = val.indexOf("\n", iPos);
		int intEr = val.indexOf("\r", iPos);
		if(intEn > intEr && intEr != -1)
		{
			intEn = intEr;
		}
		if(intEn == -1)
			intEn = val.length();
		else
			intEn++;
		return intEn;
	}

	/**
	 * Находит начало по строке sBeg и конец блока по строке sEnd находит позиции между началом и концом
	 * поиск осуществляет с позиции fromPos.y
	 * Возвращает позиции
	 * Раньше называлась getNextPart
	 *
	 * @param sBeg
	 * @param sEnd
	 * @param fromPos
	 * @return
	 */
	public PointTxt getPosBegStrToEndStr(String sBeg, String sEnd, PointTxt fromPos) {
		// Если fromPos == null - начнем поиск с начала строки
		if(fromPos == null)
			fromPos = new PointTxt();
		// Проверка на пусто
		if(sBeg !=null && !sBeg.isEmpty() && val != null)
		{
			// Начало искомого элемента
			int iPos = val.indexOf(sBeg, fromPos.y);
			int iPosEnd = 0;
			if (iPos != -1)
			{
				iPos = getEndLinePos(iPos);   // Следующая строка
				if(sEnd != null && !sEnd.isEmpty()) // Если есть строка конца
				{
					iPosEnd = val.indexOf(sEnd, iPos);
					if (iPosEnd == -1)
					{
						iPosEnd = val.length();
					} else
					{
						// Взять предыдцщую строку
					}
				} else
					iPosEnd = val.length();
				fromPos = new PointTxt(iPos, iPosEnd);
//				fromPos.e = getEndLinePos(iPosEnd);
			} else
				fromPos = new PointTxt();
		} else
			fromPos = new PointTxt();
		return fromPos;
	}

	/**
	 * Возвратит все вхождения данной переменной через AM
	 * @param sKey
	 * @param sDelim
	 * @return
	 */
	public String getVals(String sKey, String sDelim) {
		StringBuilder sb = new StringBuilder();
		PointTxt pos = new PointTxt();
		String val;
		while((val = getFirstVal(sKey, sDelim, pos)) != null) {
			sb.append(M.am).append(val);
		}
		if (sb.length() > 0)
			sb.delete(0, M.am.length());
		return(sb.toString());
	}
	public String getVals(String sKey) {
		return(getVals(sKey, null));
	}

	public String getFirstVal(String sKey, String sDelim, PointTxt frmPos) {
		String sRet = "";
		if(sDelim == null || sDelim.isEmpty()) {
			sDelim = ":=";
		}
		sRet = getFirstLine(sKey, frmPos);
		if(sRet != null) {
			sRet = sRet.substring(sKey.length());
			sRet = util.trimLR(sRet);
			int iPos;
			while ((iPos = sDelim.indexOf(sRet.substring(0, 1))) != -1) {
				sRet = sRet.substring(1);
			}
			sRet = util.trimLR(sRet);
		}
		return sRet;
	}
	public String getFirstVal(String sKey, String sDelim) {
		return(getFirstVal(sKey, sDelim, null));
	}

	/**
	 * Поиск значения по ключу  - значение от параметра до конца строки
	 * Возвращается найленная строка с начала поиска sKey до конца строки
	 * в точку помещается начало строки в которой нашли и конец этой строки в общем буфере, в котором искали
	 * @param sKey
	 * @return
	 */
	public String getFirstLine(String sKey, PointTxt frmPos) {
		String sRet = null;
		if(frmPos == null) {
			frmPos = new PointTxt();
		}
//		int ind = val.indexOf(sKey, frmPos.x);
		int ind = val.indexOf(sKey, frmPos.y);
		if(ind != -1)	// Условие выхода
		{
			frmPos.x = ind;
			// Найдем позицию окончания найденного
			frmPos.y = frmPos.x + sKey.length();
			// Найдем окончание строки
			frmPos.e = getEndLinePos(frmPos.y) - 1;
			// Выделим найденный фрагмент с начала найденного до конца строки
			sRet = val.substring(ind, frmPos.e);
			// найдем начало строки в которой нашли
			for(int i = ind; i >= 0; i--) {
				char ch = val.charAt(i);
				if(val.charAt(i) == '\r' || val.charAt(i) == '\n') {
					frmPos.b = i + 1;
					break;
				}
				if(i == 0) {
					frmPos.b = 0;
				}
			}
		}
		return sRet;
	}
	public String getFirstLine(String sKey) {
		return(getFirstLine(sKey, null));
	}

	/**
	 * Пройти по буферу, если встретится ошибка, то подкинуть ее
	 * Пройдем по всему буферу и проанализируем
	 *
	 * @return
	 */
	public AnswerBuffer checkFirstError() {

		return this;
	}

//	public AnswerBuffer parceCiscoAnswer() {
//		int iPos = 0;
//		ArrayList<posAnswerCisco> alPos = new ArrayList<>();
//
//		while((iPos = val.indexOf("! COMMAND_OUTPUT BEGIN", iPos)) != -1) {
//			// Получим начало всех команд
//			int iPosEnd = val.indexOf("! COMMAND_OUTPUT END", iPos);
//			iPosEnd = iPosEnd + "! COMMAND_OUTPUT END".length();
//			if(iPosEnd == -1) {
//				iPosEnd = val.length();
//			}
//			// Получим начало запрашиваемой команды для выделения команды cisco
//			int iPosQueryB = val.indexOf("! COMMAND BEGIN", iPos);
//			iPosQueryB = val.indexOf("\n", iPosQueryB) + 1;
//			int iPosQueryE = val.indexOf("! COMMAND END", iPosQueryB) - 1;
//
//			alPos.add(new posAnswerCisco(iPos, iPosEnd, iPosQueryB, iPosQueryE));
//			iPos = iPosEnd;
//		}
//		alPos.forEach(n -> System.out.println("\n---------------\n"
//				+ val.substring(n.posCmdB, n.posCmdE)
//				+ "\n**\n"
//				+ val.substring(n.posQueryB, n.posQueryE)
//				+ "\n\n"));
//		return this;
//	}

	public String substring(PointTxt n) {
		if(n.x != -1 && n.y != -1)
		{
			return val.substring(n.x, n.y);
		} else
			return "";
	}

	public class posAnswerCisco {
		int posCmdB = 0;
		int posCmdE = 0;

		int posQueryB = 0;
		int posQueryE = 0;

		public posAnswerCisco(int posCmdB, int posCmdE) {
			this.posCmdB = posCmdB;
			this.posCmdE = posCmdE;
		}

		public posAnswerCisco(int posCmdB, int posCmdE, int posQueryB, int posQueryE) {
			this.posCmdB = posCmdB;
			this.posCmdE = posCmdE;
			this.posQueryB = posQueryB;
			this.posQueryE = posQueryE;
		}
	}

	public String getData(int iData, ArrayList<PointTxt> data) throws Exception {
		String sRet = null;
		if(iData == -1) {
			iData = data.size() - 1;
		}
		if(iData <= data.size()) {
			sRet = util.trimLR(val.substring(data.get(iData).x, data.get(iData).y), " \n\r\t");
		} else {
			throw new Exception("Выход за пределы индекса массива. Обращение к " + iData + " элементу массива\n" + data);
		}
		return sRet;
	}
	public void checkErrors() throws Exception {
	}

	@Override
	public String toString()
	{
		return val.toString();
	}

	public MyTableModel getTable(String sBeg, Pattern pat, String sEnd, int fromPos, String sDict) {
		MyTableModel tab = new MyTableModel();

		// Получение начала строки откуда будем парсить таблицу
		PointTxt p = new PointTxt(0, fromPos);
		String sHeader = getFirstLine(sBeg, p);
		PointTxt pPos = new PointTxt(p);
		p.y = p.x;
		// Аолучение окончания того в чем будет парсить таблицу
		p = getPosBegStrToEndStr(sBeg, sEnd, p);
		pPos.y = p.y;

		ArrayList<Integer> pos = new ArrayList<Integer>();
		boolean flagHead = false;
		StringBuilder sbDict = new StringBuilder();
//		System.out.println(substring(pPos));

		for(String sLine: substring(pPos).split("\n", -1)) {
			if(!flagHead)
			{   // Ищем заголовок
				Matcher m = pat.matcher(sLine);
				if (m.find())
				{    // Строка заголовка найдена надо ее распарсить по позициям начала столбцов. Начало столбца - начало очередной группы выделения
					for (int i = 2; i <= m.groupCount(); i++)    // Идем по группам, начало каждой группы - начало столбца
					{
						pos.add(m.start(i));
						String sName = m.group(i - 1);
						if(sName != null && !sName.isEmpty()) {
							sbDict.append(";").append(sName);
						}
					}
					sbDict.append(";").append(m.group(m.groupCount())); // Добавили последний заголовок
					flagHead = true;
					if(sbDict.length() > 0)
						sbDict.delete(0, 1);    // Удалили лидирующее чтока с запятой
				}
			} else {
				// Если словарь пуст, то заменил его именами столбцов
				if(sDict == null || sDict.isEmpty())
					sDict = sbDict.toString();
				String[] dict = sDict.split(";", -1);
				ItemData item = new ItemData(tab.getDict());
				int ppp = 0;
				int len = sLine.length();
				for(int  i = 0; i < pos.size(); i++)
				{
					if(len < pos.get(i))
					{
						item.addAlways(dict[i], "");
					} else
					{
						item.addAlways(dict[i], sLine.substring(ppp, pos.get(i)).trim());
					}
					ppp = pos.get(i);
				}
				if(len < ppp)
				{
					item.addAlways(dict[dict.length - 1], "");
				} else
				{
					item.addAlways(dict[dict.length - 1], sLine.substring(ppp).trim());
				}
				tab.addItem(item);
			}
		}
		return tab;
	}

	/**
	 * Получаем из вывода таблицу, если строка подходит под паттерн.
	 * Необходимо выделить предварительно из вывода только табличную часть.
	 * должен быть паттерн, который однозначно выделит элементы таблицы
	 * @param inPatTable
	 * @return
	 */
	public MyTableModel getTablePart(int fromPos, Pattern inPatTable, String sDict)
	{
		MyTableModel tab = new MyTableModel();
		if(sDict != null && !sDict.isEmpty())
		{
			for (String sWord : sDict.split(";"))
			{
				if(!sWord.isEmpty())
					tab.getDict().addField(sWord);
			}
		}
		String [] lines = val.substring(fromPos).split("[\\n\\r]");
		for(String sLine: lines)
		{
			Matcher m = inPatTable.matcher(sLine);
			if(m.find())
			{	// Строка нам подходит - загоним ее в таблицу
				ItemData item = new ItemData(tab.getDict());
				for(int i = 1; i <= m.groupCount(); i++)	// Идем по группам, начало каждой группы - начало столбца
				{
					item.add(i - 1, m.group(i));
				}
				tab.addItem(item);
			}
			int y = 0;
		}
		return tab;
	}

}
