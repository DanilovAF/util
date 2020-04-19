package ru.DanilovAF.util.rep;

import ru.DanilovAF.util.M;
import ru.DanilovAF.util.util;

import java.awt.*;
import java.util.ArrayList;

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

	protected ArrayList<Point> alCmd = new ArrayList<>();
	protected ArrayList<Point> alRet = new ArrayList<>();
	protected ArrayList<Point> alErr = new ArrayList<>();

	public boolean isEmpry() {
		if(alCmd.isEmpty() && alRet.isEmpty() && alErr.isEmpty()) {
			return true;
		}
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
	public ArrayList<Point> getAllPart(String sBeg, String sEnd, Point fromPos) {
		if (fromPos == null)
			fromPos = new Point();
		ArrayList<Point> alRet = new ArrayList<>();

		while ((fromPos = getNextPart(sBeg, sEnd, fromPos)).x != 0) {
			alRet.add(new Point(fromPos));
		}
		return alRet;
	}

	/**
	 * Получить начало следующей строки
	 * @param iPos
	 * @return
	 */
	public int getNextLine(int iPos) {
		int intEn = val.indexOf("\n", iPos);
		int intEr = val.indexOf("\r", iPos);
		if(intEn < intEr)
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
	 * Находит начало по строке sBeg и конец блока по строке sEnd
	 * Возвращает позиции
	 * @param sBeg
	 * @param sEnd
	 * @param fromPos
	 * @return
	 */
	public Point getNextPart(String sBeg, String sEnd, Point fromPos) {
		if(fromPos == null)
			fromPos = new Point();
		int iPos = val.indexOf(sBeg, fromPos.y);
		int iPosEnd = 0;
		Point p;
		if(iPos != -1) {
			iPos = getNextLine(iPos);   // Следующая строка
			iPosEnd = val.indexOf(sEnd, iPos);
//			iPosEnd = iPosEnd + sEnd.length();
			if(iPosEnd == -1) {
				iPosEnd = val.length();
			} else {
				// Взять предыдцщую строку
			}
			fromPos = new Point(iPos, iPosEnd);
		} else
			fromPos = new Point();

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
		Point pos = new Point(0, 0);
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

	public String getFirstVal(String sKey, String sDelim, Point frmPos) {
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
	 * Во вторую координату помещаем начало найденной строки
	 * @param sKey
	 * @return
	 */
	public String getFirstLine(String sKey, Point frmPos) {
		String sRet = null;
		if(frmPos == null) {
			frmPos = new Point(0, 0);
		}
		int ind = val.indexOf(sKey, frmPos.x);
		if(ind != -1)	// Условие выхода
		{
			frmPos.x = ind + 1;
			// Найдем окончание строки
			int intEn = val.indexOf("\n", ind + sKey.length());
			int intEr = val.indexOf("\r", ind + sKey.length());
			if(intEn > intEr)
			{
				intEn = intEr;
			}
			if(intEn == -1)
			{
				intEn = val.length();
			}
			sRet = val.substring(ind, intEn);

			for(int i = ind; i >= 0; i--) {
				char ch = val.charAt(i);
				if(val.charAt(i) == '\r' || val.charAt(i) == '\n') {
					frmPos.y = i + 1;
					break;
				}
				if(i == 0) {
					frmPos.y = 0;
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
	 * PARSE_ERROR=
	 *
	 * @return
	 */
	public AnswerBuffer checkFirstError() {

		int iPos = 0;
		while((iPos = val.indexOf("PARSE_ERROR=", iPos)) != -1) {
			int iPosEnd = val.indexOf("\n", iPos);
			if(iPosEnd == -1) {
				iPosEnd = val.length();
			}
			String sBuf = val.substring(iPos + "PARSE_ERROR=".length(), iPosEnd);
			System.out.println(sBuf);
			iPos = iPosEnd;
		}
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

	public String substring(Point n) {
		return val.substring(n.x, n.y);
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

	public String getCmd(int iCmd) throws Exception {
		return getData(iCmd, alCmd);
	}
	public int getCountCmd() {
		return alCmd.size();
	}
	public String getRet(int iRet) throws Exception {
		return getData(iRet, alRet);
	}
	public int getCountRet() {
		return alRet.size();
	}
	public String getData(int iData, ArrayList<Point> data) throws Exception {
		String sRet = null;
		if(iData < data.size()) {
			sRet = util.trimLR(val.substring(data.get(iData).x, data.get(iData).y), " \n\r\t");
		} else {
			throw new Exception("Выход за пределы индекса массива. Обращение к " + iData + " элементу массива\n" + data);
		}
		return sRet;
	}
	public String getErr(int iErr) throws Exception {
		String sRet = getData(iErr, alErr);
		sRet = util.field(sRet, "=", 2);
		sRet = util.trimLR(sRet, "\"");
		return sRet;
	}
	public int getCountErr() {
		return alErr.size();
	}
	public void checkErrors() throws Exception {
	}
}
