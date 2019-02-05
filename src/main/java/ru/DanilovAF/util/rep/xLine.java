package ru.DanilovAF.util.rep;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 22.01.2010
 * Time: 15:33:17
 * Класс описывает одну найденную линию в рапорте
 */
public class xLine
{
	private int index = 0;
	private String line;	// Сама найденная строка от начала вхождения до символа окончания строки

	public xLine(int index, String line)
	{
		this.index = index;
		this.line = line;
	}

	public int getIndex()
	{
		return index;
	}

	public void setIndex(int index)
	{
		this.index = index;
	}

	public String getLine()
	{
		return line;
	}

	public void setLine(String line)
	{
		this.line = line;
	}

	@Override
	public String toString()
	{
		return line;
	}
}
