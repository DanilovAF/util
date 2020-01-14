package ru.DanilovAF.util.rep;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 22.01.2010
 * Time: 15:23:18
 * Класс держатель неких текстовых данных и основные операции на ними
 */
public class Rep
{
	private StringBuffer rep;

	public Rep()
	{
		this.rep = new StringBuffer();
	}

	public Rep(String rep)
	{
		this.rep = new StringBuffer(inputConvert(rep));
	}

	/**
	 * При создании можно конвертировать данные
	 * @param rep
	 * @return
	 */
	protected String inputConvert(String rep) {
		return rep;
	}

	public Rep(StringBuffer rep)
	{
		this.rep = new StringBuffer(inputConvert(rep.toString()));
	}

	public StringBuffer getRep()
	{
		return rep;
	}

	public void clear()
	{
		if(rep != null)
		{
			rep.setLength(0);
		}
	}
	/**
	 * Ответить содержится ли в рапорте указанная подстрока
	 * @param in_s что ищем
	 * @return если долержитс, то истина
	 */
	public boolean isHave(String in_s)
	{
		int ind = rep.indexOf(in_s);
		return((ind != -1));
	}
	/**
	 * Найти и выдать все строки, в которых содержиться указанный паттерн
	 * @param inPat паттерн
	 * @param pos с какой позиции искать
	 * @param count с какого вхождения находить строки
	 * @return список строк
	 */
	public ArrayList<xLine> indexOf(String inPat, int pos, int count)
	{
		ArrayList<xLine> lRet = new ArrayList<xLine>();
		int i = 1;
		// Поиск вхождений
		while(true)
		{
			int ind = rep.indexOf(inPat, pos);
			if(ind != -1)	// Условие выхода
			{
				if(i >= count)	// Нужное вхождение по счету
				{
					// Найдем окончание строки
					int intEn = rep.indexOf("\n", ind + inPat.length());
					int intEr = rep.indexOf("\r", ind + inPat.length());
					if(intEn < intEr)
					{
						intEn = intEr;
					}
					if(intEn == -1)
					{
						intEn = rep.length();
					}
					lRet.add(new xLine(ind, rep.substring(ind, intEn)));
					pos = intEn;
				}
			} else
			{
				break;
			}
		}
		return(lRet);
	}
	public ArrayList<xLine> indexOf(String inPat, int pos)
	{
		return(indexOf(inPat, pos, 1));
	}
	public ArrayList<xLine> indexOf(String inPat)
	{
		return(indexOf(inPat, 0, 1));
	}

	@Override
	public String toString()
	{
		return rep.toString();	//To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * Найти вхождение строки in_s и вернуть то, что осталось до конца строки
	 * @param in_s что ищем
	 * @return возвращяем остаток от того что нашли до конца строки, возвращаем последнее значение этого элемента
	 */
	public String getVal(String in_s)
	{
		String sRet = "";
		ArrayList<xLine> buf = indexOf(in_s);
		if(buf != null && buf.size() != 0)
		{
			sRet = buf.get(buf.size() - 1).getLine().substring(in_s.length());
		}
		return sRet;
	}

	/**
	 * Найти вхождение строки in_s и вернуть то, что осталось до конца строки
	 * @param inPat что ищем
	 * @return возвращяем остаток от того что нашли до конца строки, возвращаем последнее значение этого элемента
	 */
	public String getValNextLine(String inPat)
	{
		String sRet = "";
		int ind = rep.indexOf(inPat);	// Поиск первого вхождения
		if(ind != -1)	// Условие выхода
		{
//			if(i >= count)	// Нужное вхождение по счету
			{
				// Найдем окончание строки
				int intEn = rep.indexOf("\n", ind + inPat.length());
				int intEr = rep.indexOf("\r", ind + inPat.length());
				if(intEn < intEr)
				{
					intEn = intEr;
				}
				if(intEn == -1)
				{
					intEn = rep.length();
				} else
				{
					intEn++;
					// Пропустим ближайшие перевод строки
					if(rep.length() > intEn && "\r\n".indexOf(rep.charAt(intEn+1)) != -1)
					{
						intEn++;
					}
				}
				// Получим строку следующую
				ind = intEn;
				intEn = rep.indexOf("\n", ind);
				intEr = rep.indexOf("\r", ind);
				if(intEn < intEr)
				{
					intEn = intEr;
				}
				if(intEn == -1)
				{
					intEn = rep.length();
				}
				// Теперь надо взять следующую строку
				sRet = rep.substring(ind, intEn);
			}
		}
		return sRet;
	}

	public ArrayList<ArrayList<String>> getTable(String sBeg, Pattern pat, String sEnd)
	{
		ArrayList<ArrayList<String>> aRet = new ArrayList<ArrayList<String>>();
		int ind = rep.indexOf(sBeg);
		if(ind != -1)
		{
			StringTokenizer st = new StringTokenizer(rep.substring(ind), "\r\n");
			int flagHead = 0;
			ArrayList<Integer> pos = new ArrayList<Integer>();
			while(st.hasMoreElements())
			{
				String line = st.nextToken();
				if(flagHead == 0)
				{
					Matcher m = pat.matcher(line);
					if(m.find())
					{	// Строка заголовка найдена надо ее распарсить по позициям начала столбцов + 1 пробел впереди
						int flag = 0;
						for(int i = 0; i < line.length(); i++)	// Идем по строке находим позиции в которых с пробела переход на непробел - это будут колонки
						{
							if(line.charAt(i) == ' ')
							{
								flag = 1;
							} else
							{
								if(flag == 1)	// Начало символа после пробела
								{
									pos.add(i);
									flag = 0;
								}
							}
						}
						flagHead = 1;
						int y = 0;
					}
				} else
				{	// Здесь должна быть таблица до конца рапорта
					if(line.indexOf(sEnd) == 0)
					{
						break;
					} else
					{	// Ну и парсим здесь по позициям
						ArrayList<String> aLine = new ArrayList<String>();
						int p = 0;
						int len = line.length();
						for(int  i = 0; i < pos.size(); i++)
						{
							if(len < pos.get(i))
							{
								return(aRet);
							}
							aLine.add(line.substring(p, pos.get(i)).trim());
							p = pos.get(i);
						}
						aLine.add(line.substring(p));
						aRet.add(aLine);
					}
				}
			}
		}
		return(aRet);  //To change body of created methods use File | Settings | File Templates.
	}

	/**
	 * Находит начальную строку таблицы, парсит заголовок по паттерну и до строки конца таблицы позиционно вырезает данные
	 * @param sBeg
	 * @param pat
	 * @param sEnd
	 * @return
	 */
	public ArrayList<ArrayList<String>> getTablePosition(String sBeg, Pattern pat, String sEnd)
	{
		ArrayList<ArrayList<String>> aRet = new ArrayList<ArrayList<String>>();
		int ind = rep.indexOf(sBeg);
		if(ind != -1)
		{
			StringTokenizer st = new StringTokenizer(rep.substring(ind), "\r\n");
			int flagHead = 0;
			ArrayList<Integer> pos = new ArrayList<Integer>();
			while(st.hasMoreElements())
			{
				String line = st.nextToken();
				if(flagHead == 0)
				{
					Matcher m = pat.matcher(line);
					if(m.find())
					{	// Строка заголовка найдена надо ее распарсить по позициям начала столбцов. Начало столбца - начало очередной группы выделения
						int flag = 0;
						for(int i = 2; i <= m.groupCount(); i++)	// Идем по группам, начало каждой группы - начало столбца
						{
							pos.add(m.start(i));
						}
						flagHead = 1;
						int y = 0;
					}
				} else
				{	// Здесь должна быть таблица до конца рапорта
					if(line.indexOf(sEnd) != -1)
					{
						break;
					} else
					{	// Ну и парсим здесь по позициям
						ArrayList<String> aLine = new ArrayList<String>();
						int p = 0;
						int len = line.length();
						for(int  i = 0; i < pos.size(); i++)
						{
							if(len < pos.get(i))
							{
								aLine.add("");
							} else
							{
								aLine.add(line.substring(p, pos.get(i)).trim());
							}
							p = pos.get(i);
						}
						if(len < p)
						{
							aLine.add("");
						} else
						{
							aLine.add(line.substring(p));
						}
						aRet.add(aLine);
					}
				}
			}
		}
		return(aRet);  //To change body of created methods use File | Settings | File Templates.
	}

	/**
	 * Получаем из вывода таблицу, если строка подходит под паттерн.
	 * Необходимо выделить предварительно из вывода только табличную часть.
	 * должен быть паттерн, который однозначно выделит элементы таблицы
	 * @param inPatTable
	 * @return
	 */
	public ArrayList<ArrayList<String>> getTablePart(Pattern inPatTable)
	{
		ArrayList<ArrayList<String>> aRet = new ArrayList<ArrayList<String>>();
		String [] lines = rep.toString().split("[\\n\\r]");
		for(String sLine: lines)
		{
			Matcher m = inPatTable.matcher(sLine);
			if(m.find())
			{	// Строка нам подходит - загоним ее в таблицу
				ArrayList<String> aLine = new ArrayList<String>();
				for(int i = 1; i <= m.groupCount(); i++)	// Идем по группам, начало каждой группы - начало столбца
				{
					aLine.add(m.group(i));
				}
				aRet.add(aLine);
			}
			int y = 0;
		}
		return aRet;
	}
	public ArrayList<ArrayList<String>> getTablePat(String sBeg, Pattern pat, String sEnd)
	{
		ArrayList<ArrayList<String>> aRet = new ArrayList<ArrayList<String>>();
		int ind = rep.indexOf(sBeg);
		if(ind != -1)
		{
			int ind2 = rep.indexOf(sEnd, ind);
			if(ind2 == -1) { ind2 = rep.length(); }
			Rep rEp = new Rep(rep.substring(ind, ind2));
			aRet = rEp.getTablePart(pat);
		}
		return(aRet);  //To change body of created methods use File | Settings | File Templates.
	}

}


