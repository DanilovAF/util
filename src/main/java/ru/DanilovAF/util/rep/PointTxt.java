package ru.DanilovAF.util.rep;

/**
 * Указатель в тексте на позиции
 * x - начало строки
 * y - окончание в строке
 * b - начало строки
 * e - окончание строки
 */

public class PointTxt
{
	public int x = -1;
	public int y = -1;
	public int b = -1;
	public int e = -1;

	public PointTxt()
	{
	}

	public PointTxt(int x, int y, int b, int e)
	{
		this.x = x;
		this.y = y;
		this.b = b;
		this.e = e;
	}

	public PointTxt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public PointTxt(PointTxt p)
	{
		this.x = p.x;
		this.y = p.y;
		this.b = p.b;
		this.e = p.e;
	}

}
