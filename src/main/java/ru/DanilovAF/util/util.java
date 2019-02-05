package ru.DanilovAF.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 20.10.2009
 * Time: 14:11:28
 * Класс утилитных функций, которых не хватает в стандартной Яве
 */
public class util
{
	public static boolean is64OS()
	{
		String sOs = System.getProperty("os.arch");
		boolean fRet = false;
		if(sOs != null && sOs.indexOf("64") != -1)
		{
			fRet = true;
		}
		return(fRet);
	}

	public static void addDllPath(String in_sPath) throws NoSuchFieldException, IllegalAccessException
	{
		Field field = null;

		field = ClassLoader.class.getDeclaredField("usr_paths");
		field.setAccessible(true);
		String[] paths = (String[])field.get(null);
		String[] tmp = new String[paths.length+1];
		System.arraycopy(paths,0,tmp,1,paths.length);
		tmp[0] = in_sPath;
		field.set(null,tmp);
//		field = ClassLoader.class.getDeclaredField("usr_paths");
//		field.setAccessible(true);
//		String[] paths1 = (String[])field.get(null);
	}

	/**
	 * Удаляет в буфере все пробелы, которые идут подряд - остается только один
	 * @param inSb строка над которой проводим эту операцию
	 * @return
	 */
	public static String trim(String inSb)
	{
		StringBuffer sb = new StringBuffer(inSb);
		trim(sb);
		return(sb.toString());
	}

	public static String trimLR(String inSb)
	{
		StringBuffer sb = new StringBuffer(inSb);
		trimLR(sb, " ");
		return(sb.toString());
	}

	/**
	 * Удаляет в буфере все пробелы, которые идут подряд - остается только один
	 * @param inSb стринг буфер над которым работаем
	 * @return
	 */
	public static StringBuffer trim(StringBuffer inSb)
	{
		boolean flagSp = false;
		if(inSb != null)
		{
			for(int i = inSb.length() - 1; i >= 0; i--)
			{
				if (inSb.charAt(i) == ' ')
				{
					if (flagSp)
					{ // надо удалить символ
						inSb.delete(i, i + 1);
					}
					flagSp = true;
				} else
				{
					flagSp = false;
				}
			}
		}
		return(inSb);
	}

	public static String getCurDir()
	{
		String sFile = new File(".").getAbsolutePath();
		if(sFile.indexOf("\\.") == sFile.length()-2)
		{
			sFile = sFile.substring(0, sFile.length()-1);
		}
		return sFile;
	}
	public static String getDirToSetup()
	{
		File fPath = new File(System.getenv("APPDATA") + "\\pilots");
		if(!fPath.isDirectory())
		{
			// Надо создать лиректорию
			fPath.mkdir();
		}
		return(fPath.getPath());
	}

	/**
	 * Ошибку переводим в строку. Весь стек.
	 * @param ex ошибка
	 * @return строка со стеком.
	 */
	public static String stackTrace(Exception ex)
	{
		ByteArrayOutputStream ba = new ByteArrayOutputStream(2048);
		PrintStream ps = new PrintStream(ba);	// Создали в памяти буфер под сообщение
		ex.printStackTrace(ps);	// вывели его в этот буфер
		return(ba.toString());
	}

	// Заменяет в строке
	/**
	 * @param inStr в какой строке производить замену
	 * @param repCh какие символы заменять (символы рассматриваются отдельно)
	 * @param toRepCh на какие символы заменять
	 * @return новая строка
	 */
	static public String replace(String inStr, String repCh, String toRepCh)
	{
		String sRet = "";
		for(int i = 0; i < inStr.length(); i++)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				sRet += toRepCh;
			} else
			{
				sRet += inStr.substring(i, i + 1);
			}
		}
		return(sRet);
	}

	/**
	 * Замена строки целиком
	 * @param inStr
	 * @param repCh
	 * @param toRepCh
	 * @return
	 */
	static public String replaceStr(String inStr, String repCh, String toRepCh)
	{
		String sRet = "";
		int iIndex = 0;
		int delta = repCh.length() - toRepCh.length();
		while(true)
		{
			iIndex = inStr.indexOf(repCh, iIndex);
			if(iIndex == -1)
			{
				break;
			}
			inStr = inStr.substring(0, iIndex) + toRepCh + inStr.substring(iIndex + repCh.length());
			iIndex += toRepCh.length();
		}
		return(inStr);
	}

	// Заменяет в строке
	/**
	 * @param inStr в какой строке производить замену
	 * @param repCh какие символы заменять
	 * @param toRepCh на какие символы заменять
	 * @return новая строка
	 */
	static public StringBuffer replace(StringBuffer inStr, String repCh, String toRepCh)
	{
		for(int i = inStr.length() - 1; i >= 0 ; i--)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				if(toRepCh == null || toRepCh.compareTo("") == 0)
				{	// Удаление символа
					inStr.delete(i, i + 1);
				} else
				{	// Замена символа
					inStr.replace(i, i + 1, toRepCh);
				}
			} else
			{	// А ничего не делаем, все изменение идет в самом объекте
				//sRet += inStr.substring(i, i + 1);
			}
		}
		return(inStr);
	}

	static public String replaceL(String inStr, String repCh, String toRepCh)
	{
		String sRet = "";
		for(int i = 0; i < inStr.length(); i++)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				sRet += toRepCh;
			} else
			{
				sRet += inStr.substring(i);
				break;
			}
		}
		return(sRet);
	}
	static public StringBuffer replaceL(StringBuffer inStr, String repCh, String toRepCh)
	{
		int ind = 0;
		for(int i = 0; i < inStr.length(); i++)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				ind++;
			} else
			{
				break;
			}
		}
		if(toRepCh == null || toRepCh.compareTo("") == 0)
		{	// Удаление символов
			inStr.delete(0, ind);
		} else
		{	// Замена символа
			inStr.replace(0, ind, toRepCh);
		}
		return(inStr);
	}

	static public String replaceR(String inStr, String repCh, String toRepCh)
	{
		int ind = 0;
		String sRet = "";
		for(int i = inStr.length() - 1; i >= 0; i--)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				ind++;
			} else
			{
				break;
			}
		}
		sRet = inStr.substring(0, inStr.length() - ind);
		for(int i = 0; i < ind; i++)
		{
			sRet += toRepCh;
		}
		return(sRet);
	}

	static public StringBuffer replaceR(StringBuffer inStr, String repCh, String toRepCh)
	{
		int ind = 0;
		for(int i = inStr.length() - 1; i >= 0; i--)
		{
			if(repCh.indexOf(inStr.substring(i, i + 1)) != -1)
			{	// Надо менять
				ind++;
			} else
			{
				break;
			}
		}
		if(toRepCh == null || toRepCh.compareTo("") == 0)
		{	// Удаление символа
			inStr.delete(inStr.length() - ind, inStr.length());
		} else
		{	// Замена символа
			inStr.replace(inStr.length() - ind, inStr.length(), toRepCh);
		}
		return(inStr);
	}
	static public StringBuffer replaceLR(StringBuffer inStr, String repCh, String toRepCh)
	{
		replaceL(inStr, repCh, toRepCh);
		replaceR(inStr, repCh, toRepCh);
		return(inStr);
	}
	static public StringBuffer trimLR(StringBuffer inStr, String repCh)
	{
		replaceLR(inStr, repCh, "");
		return(inStr);
	}
	/**
	 * Функция для выделения подстроки между указанными разделителями.
	 * @param in_sData	Строка из которой выделять
	 * @param in_sDelim	Разделитель
	 * @param in_intCount	Номер выделения по счету
	 * @return	Выделенная между разделителями строка
	 */
	public static String field(String in_sData, String in_sDelim, int in_intCount)
	{
		String sRet = "";
		int intPosDelta = 0;
		int index = 0;
		int intCount = 0;
		if(in_sData != null)
		{
			while (true)
			{
				intCount++;    // Кол-во вхождений подняли на 1
				if (intCount == in_intCount)
				{    // Надо ывделить и закончить обработку
					// Начало строки выделения intPosDelta
					index = in_sData.indexOf(in_sDelim, intPosDelta);    // Ищем начало следующего по порядку разделителя
					if (index != -1)
					{
						//index += intPosDelta;	// Окончание строки выделения
					} else
					{
						index = in_sData.length();    // Начало Следующего разделителя не нашли - значит до конца строки
					}
					sRet = in_sData.substring(intPosDelta, index);
					break;
				} else
				{
					index = in_sData.indexOf(in_sDelim, intPosDelta);
					if (index == -1)
						break;
					intPosDelta = index + in_sDelim.length();
				}
			}
			if (in_intCount == -1)
			{
				sRet = in_sData.substring(intPosDelta);
			}
		}
		return sRet;
	}

	/**
	 * Преобразование из строки только цыфры
	 * @param in_schar Строка
	 * @param in_drob знак дроби
	 * @return Возвращается число
	 */
	public static double mcn(String in_schar, char in_drob)
	{
		double dRet = 0;
		int lenDrob = 0;
		int znak = 1;
		if(in_schar != null && in_schar.compareTo("") != 0)
		{
			// Сначала надо оставить только цифры
			String mBuf;
			for(int i = 0; i < in_schar.length(); i++)
			{
				char ch;
				ch = in_schar.charAt(i);
				if(ch >= '0' && ch <= '9')
				{
					dRet = dRet * 10 + (ch - '0');
					if(lenDrob != 0)
						lenDrob *=10;	// Если определена дробная часть, надо увеличить на 1
				} else if(ch == in_drob)
				{
					lenDrob = 1;
				} else if(ch == '-' && dRet == 0)
				{
					znak = -znak;
				}
			}
			// Отработаем дробную часть
			if(lenDrob == 0)
				lenDrob = 1;
			dRet = (dRet/lenDrob)*znak;
		}
		return(dRet);
	}

	/**
	 * Получим из строки число и вернем его как строку
	 * @param in_schar строка из которой извлекаем число
	 * @param in_drob знак, определяющий разделитель дроби
	 * @return
	 */
	public static String mcnS(String in_schar, char in_drob)
	{
		double dRet = 0;
		StringBuffer sRet = new StringBuffer();
		int lenDrob = 0;
		int znak = 1;
		if(in_schar != null && in_schar.compareTo("") != 0)
		{
			// Сначала надо оставить только цифры
			String mBuf;
			for(int i = 0; i < in_schar.length(); i++)
			{
				char ch;
				ch = in_schar.charAt(i);
				if(ch >= '0' && ch <= '9')
				{
					sRet.append(ch);
					dRet = dRet * 10 + (ch - '0');
					if(lenDrob != 0)
						lenDrob ++;	// Если определена дробная часть, надо увеличить на 1
				} else if(ch == in_drob)
				{
					lenDrob = 1;
				} else if(ch == '-' && dRet == 0)
				{
					znak = -znak;
				}
			}
			// Отработаем дробную часть
			if(lenDrob != 0)
			{
				dRet = (dRet/(10^lenDrob))*znak;
				sRet.insert(sRet.length()-lenDrob + 1, ".");
			}
			if(znak < 0 && sRet.length() > 0)
			{
				sRet.insert(0, "-");
			}
		}
		return(sRet.toString());
	}

	/**
	 * Число преобразует в IP адрес
	 * @param in_iIP число
	 * @return строка как IP адрес
	 */
	public static String int2ip(long in_iIP)
	{
		long iIP[] = new long[4];
		iIP[0]=(long)(in_iIP/256/256/256) * 256 * 256 * 256;
		iIP[1]=(long)((in_iIP-iIP[0])/256/256) * 256 * 256;
		iIP[2]=(long)((in_iIP-iIP[0]-iIP[1])/256) * 256;
		iIP[3]=in_iIP-iIP[0]-iIP[1]-iIP[2];

		iIP[0]=(iIP[0]/256/256/256);
		iIP[1]=(iIP[1]/256/256);
		iIP[2]=(iIP[2]/256);
		return "" + iIP[0] + "." + iIP[1] + "." + iIP[2] + "." + iIP[3];
	}

	/**
	 * Преобразует IP адрес в виде строки в число
	 * @param in_sIP число IP адреса
	 * @return
	 */
	public static long ip2int(String in_sIP)
	{
		StringTokenizer st = new StringTokenizer(in_sIP, ".");
		String num[] = in_sIP.split("[.]");
		long iRet = 0 ;
		long iMnozh = 256 * 256 * 256;
		for(String sBuf: num)
		{
		    iRet += mcn(sBuf, '.') * iMnozh;
			iMnozh = iMnozh / 256;
		}
		 
		return(iRet);
	}

	public static void main(String[] args)
	{
//		System.out.println(divOnToken("NAME: \"Switch System\", DESCR: \"Cisco Systems, Inc. WS-C4507R+E 7 slot switch \"", ",", null));
//		for(String ss: divOnToken(",проба,, пера, да,\",номер один два,\", три, четыре,", ",", null))
//		{
//			System.out.println("<" + ss + ">");
//		}

		String sIp = "255.255.255.0";
		long ll = util.ip2int(sIp);
		System.out.println(ll);
		sIp = util.int2ip(ll);
		System.out.println(sIp);
//		Math.pow(2, 24);
	}

	/**
	 * Поделим на токены строку с учетом кавычек
	 * @param sLine Строка, которую делим
	 * @param sDiv Разделитель на токены
	 * @param sKavich что может быть в качестве кавычек
	 * @return массив из строк
	 */
	public static ArrayList<String> divOnToken(String sLine, String sDiv, String sKavich)
	{
		if(sKavich == null)
		{
			sKavich = "\"";
		}
		ArrayList<String> alRet = new ArrayList<String>();
		ArrayList<Integer> alTok = new ArrayList<Integer>();
		ArrayList<Integer> alKav = new ArrayList<Integer>();
		int iPos = 0;
//		alTok.add(0);
		while((iPos = sLine.indexOf(sDiv, iPos)) != -1)
		{
			alTok.add(iPos);
			alTok.add(iPos + sDiv.length());
			iPos += sDiv.length();
		}
//		alTok.add(sLine.length() - 1);
		while((iPos = sLine.indexOf(sKavich, iPos)) != -1)
		{
			alKav.add(iPos);
			iPos += sKavich.length();
		}
//		System.out.println(alTok);
//		System.out.println(alKav);
		for(int i = alKav.size() - 1; i > 0; i = i - 2)
		{
			int iMax = alKav.get(i);
			int iMin = alKav.get(i - 1);
			for(int k = alTok.size() - 1; k >= 0; k--)
			{
				int iVal = alTok.get(k);
				if(iVal > iMin && iVal < iMax + 1)
				{	// Надо удалить разделитель
					alTok.remove(k);
				}
			}
		}
//		System.out.println(alTok);
//		System.out.println(alKav);

		alTok.add(0, 0);
		alTok.add(sLine.length());
		for(int i = 0; i < alTok.size(); i = i + 2)
		{
			int iPos1 = alTok.get(i);
			int iPos2 = alTok.get(i + 1);
			alRet.add(sLine.substring(iPos1, iPos2));
		}
		return alRet;
	}

	/**
	 * Получение текущей даты в формате PICK
	 * @return целое число дней с 1970 года
	 */
	public static int getCurDate()
	{
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Calendar calEnd = Calendar.getInstance();
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		long lEnd = calEnd.getTimeInMillis();

		try
		{
			calEnd.setTime(format.parse("01.01.1970"));
		} catch(ParseException e)
		{
			// Проглотим ошибку, т.к. ее быть не должно
//			e.printStackTrace();
		}
		long lBeg = calEnd.getTimeInMillis();
		int iDeys = (int) ((lEnd - lBeg) / (24 * 60 * 60 * 1000));
		return iDeys;
	}

	/**
	 * Получение указанной даты в формате PICK
	 * @param inDate Дата , которую надо перевестив формат PICK
	 * @return
	 */
	public static int getDate(Date inDate)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(inDate);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		long lEnd = calEnd.getTimeInMillis();

		try
		{
			calEnd.setTime(format.parse("01.01.1970"));
		} catch(ParseException e)
		{
			// Проглотим ошибку, т.к. ее быть не должно
//			e.printStackTrace();
		}
		long lBeg = calEnd.getTimeInMillis();
		int iDays= (int) ((lEnd - lBeg) / (24 * 60 * 60 * 1000));
		return iDays;
	}

	/**
	 * Получение текущего времени в формате PICK
	 * @return целое число секунд с начала дня
	 */
	public static int getCurTime()
	{
		Calendar calEnd = Calendar.getInstance();
		Calendar calBeg = Calendar.getInstance();
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		long lEnd = calEnd.getTimeInMillis();
		long lBeg = calBeg.getTimeInMillis();
		int iSec= (int) ((lBeg - lEnd) / (1000));

		return iSec;
	}

	public static int getTime(Date inDate)
	{
		Calendar calEnd = Calendar.getInstance();
		calEnd.setTime(inDate);
		Calendar calBeg = Calendar.getInstance();
		calBeg.setTime(inDate);
		calEnd.set(Calendar.HOUR_OF_DAY, 0);
		calEnd.set(Calendar.MINUTE, 0);
		calEnd.set(Calendar.SECOND, 0);
		calEnd.set(Calendar.MILLISECOND, 0);
		long lEnd = calEnd.getTimeInMillis();
		long lBeg = calBeg.getTimeInMillis();
		int iSec= (int) ((lBeg - lEnd) / (1000));

		return iSec;
	}

	/**
	 * Реверс байта типа
	 * 0010 1000 -> 0001 0100
	 * @param x
	 * @return
	 */
	public static byte reverseBitsByte(byte x) {
		int intSize = 8;
		byte y=0;
		for(int position=intSize-1; position>=0; position--){
			y+=((x&1)<<position);
			x >>= 1;
		}
		return y;
	}

	/**
	 * Возвращает расширение файла
	 * @param inFile
	 * @return
	 */
	public static String getExtFile(File inFile)
	{
		String sFileName = inFile.getName();
		String sFileExt = util.field(inFile.getName(), ".", -1);
		if(sFileName.compareTo(sFileExt) == 0)
		{	// Нет расширения, т.к. имя файла равно расширению
			sFileExt = "";
		}
		return(sFileExt);
	}

	/**
	 * Заменяет расширение файла на указанное
	 * @param inFile
	 * @param sNewExt новое расширение
	 * @return Возвращает строку новое имя файла с путем
	 */
	public static String getFileNameNewExt(File inFile, String sNewExt)
	{
		String sFilePath = inFile.getPath();
		String sFileName = inFile.getName();
		String sFileExt = getExtFile(inFile);

		String sFileSpr = sFileName.substring(0, sFileName.length()-sFileExt.length()) + sNewExt;	// Имя файла с новым расширением
		String sFilePathSpr = sFilePath.substring(0, sFilePath.length() - sFileName.length()) + sFileSpr;	// Путь до файла с новым расширением
		return(sFilePathSpr);
	}

	/**
	 * Преобрадовать строку их формата 01D3DF  в строку ASCII
	 * @param in_s
	 * @return
	 */
	public static String hexStringTostring(String in_s) throws UnsupportedEncodingException {
		int n = in_s.length();
		byte [] bb = new byte[n/2];
		int y = 0;
		for (int i = 0; i < n; i += 2) {
			char a = in_s.charAt(i);
			char b = in_s.charAt(i + 1);

			int ia = hexToInt(a);
			int ib = hexToInt(b);
			int iab = (ia << 4) + ib;
			bb[y++] = (byte) iab;
		}
		String ssRet = new String(bb, "UTF8");
		return ssRet;
	}
	private static int hexToInt(char ch) {
		if ('a' <= ch && ch <= 'f') { return ch - 'a' + 10; }
		if ('A' <= ch && ch <= 'F') { return ch - 'A' + 10; }
		if ('0' <= ch && ch <= '9') { return ch - '0'; }
		throw new IllegalArgumentException(String.valueOf(ch));
	}

	public static String bytesToHex(byte[] array, int iBeg, int iLen)
	{
		if(iLen == -1)
		{
			iLen = array.length - iBeg;
		}

//		char[] val = new char[2*(iLen)];
		char[] val = new char[2];
		String hex = "0123456789ABCDEF";
		StringBuffer sb = new StringBuffer();
		for (int i = iBeg; i < iBeg + iLen; i++)
		{
			int b = array[i] & 0xff;
//			val[2*(i - iBeg)] = hex.charAt(b >>> 4);
			val[0] = hex.charAt(b >>> 4);
//			val[2*(i - iBeg) + 1] = hex.charAt(b & 15);
			val[1] = hex.charAt(b & 15);
			sb.append(String.valueOf(val)).append(" ");
		}
		return sb.toString();
	}

	/**
	 * Получает нижний полубайт
	 * @param inByte
	 * @return
	 */
	public static int getLowOctet(byte inByte)
	{
		char ch = (char) ((inByte & 0xff));
		int  chUp = ch >> 4;
		int  chDw = ch - (chUp << 4);

		return(chDw);
	}

	/**
	 * Получает верхний полубайт как число
	 * @param inByte
	 * @return
	 */
	public static int getUpOctet(byte inByte)
	{
		char ch = (char) ((inByte & 0xff));
		int  chUp = ch >> 4;
//		int  chDw = ch - (chUp << 4);

		return(chUp);
	}

	/**
	 * Преобразует char в байт - 2 байта
	 * @param inCh	чар для преобразования
	 * @param inoutByte куда писать преобразованное
	 * @param iPos с какой позиции писать
	 * @return
	 */
	public static byte convertChar2Byte(char inCh, byte[] inoutByte, int iPos)
	{
		inoutByte[iPos] = (byte) ((inCh&0xFF00)>>8);
		inoutByte[iPos + 1] = (byte) (inCh&0x00FF);
		return inoutByte[iPos];
	}

	/**
	 * Получить из нужной длинны байт целое. Кол-во байт от 1 до 4 далее пойдет переполнение
	 * @param array
	 * @param iBeg
	 * @param iLen
	 * @return
	 */
	public static int getIntFromByte(byte[] array, int iBeg, int iLen)
	{
		int iRet = 0;
		for(int i = 0; i < iLen; i++)
		{
			iRet = iRet << 8;	// Сдвинули разряд
			char ch = (char) ((array[iBeg + i] & 0xff));	// Добавили байт
			iRet += ch;
		}
		return iRet;
	}

	public static String alignString(String inS, String sF, int iLen)
	{
	    StringBuffer sb = new StringBuffer();

		for(int i = inS.length(); i < iLen; i++)
		{
			sb.append(sF);
		}
		sb.append(inS);
		return sb.toString();
	}

	public static String alignStringLeft(String inS, String sF, int iLen)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(inS);
		for(int i = inS.length(); i < iLen; i++)
		{
			sb.append(sF);
		}
		return sb.toString();
	}

	public static String getUserName()
	{
		return System.getProperties().getProperty("user.name");
	}

	public static Properties getProp(String sParce, String inDelim)
	{
		Properties pRet = new Properties();
		for(String s: sParce.split("\r"))
		{
			String sKey, sVal;
			s = util.replace(s, "\n", "");
			int iPos = s.indexOf(inDelim);
			if(iPos != -1)
			{
				sKey = s.substring(0, iPos);
				sVal = s.substring(iPos + inDelim.length());
			} else
			{
				sKey = s;
				sVal = "";
			}
			pRet.setProperty(sKey, sVal);
		}
		return pRet;
	}

	public static boolean isHexString(String in_s)
	{
		boolean bRet = false;
//		StringBuffer sb = new StringBuffer();
		if(in_s != null && !in_s.isEmpty()) {
			bRet = true;
			for (String sDiv : in_s.split(":"))
			{
				if(sDiv.length() == 2)
				{
					if(!((sDiv.charAt(0) >= '0' && sDiv.charAt(0) <= '9') || (sDiv.charAt(0) >= 'a' && sDiv.charAt(0) <= 'f') || (sDiv.charAt(0) >= 'A' && sDiv.charAt(0) <= 'F')))
					{
							bRet = false;
							break;
					}
					if(!((sDiv.charAt(1) >= '0' && sDiv.charAt(1) <= '9') || (sDiv.charAt(1) >= 'a' && sDiv.charAt(1) <= 'f') || (sDiv.charAt(1) >= 'A' && sDiv.charAt(1) <= 'F')))
					{
						bRet = false;
						break;
					}
//					sb.append(sDiv).append(":");
				} else
				{
					bRet = false;
					break;
				}
			}
//			int y = sb.length();
//			sb.delete(y - 1, y);
		}
		return bRet;
	}

	/**
	 * Подсчитать кол-во вхождений строки
	 * @param sVal
	 * @param vm
	 * @return
	 */
	public static int count(String sVal, String vm) {
		int iCount = 0;
		int iPos = 0;
		if(sVal != null) {
			while ((iPos = sVal.indexOf(vm, iPos)) != -1) {
				iCount++;
				iPos++;
			}
		}
		return iCount;
	}
}
















