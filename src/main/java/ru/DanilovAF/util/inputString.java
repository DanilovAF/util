package ru.DanilovAF.util;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 21.06.2006
 * Time: 16:46:10
 * Класс эмулирует входной поток в виде строк. Нужен для того чтобы парсер рапортов получал данные по одной строке
 * Таким образом легко распарсить файл, а также рапорта от станции.
 * Фишка в функции nextString, она выдает следующую строку, пока не очистится буфер
 */
public class inputString
{
	private ArrayList<String> m_asReps = new ArrayList<String>();
	private int m_intCurPos = 0;
	BufferedReader m_brFile;


	public inputString(String in_s)
	{
		set_Data(in_s);
	}

	public inputString(File in_fReps) throws FileNotFoundException
	{
		m_brFile = new BufferedReader(new FileReader(in_fReps));
	}

	public void toBegin()
	{
		m_intCurPos = 0;
	}

	public void toPos(int in_intPos)
	{
		m_intCurPos = in_intPos;
	}

	public void close() throws IOException
	{
		if(m_brFile != null)
		{
			m_brFile.close();
		} else
		{
			m_asReps.clear();
		}
		m_intCurPos = 0;
	}

	public String nextString() throws IOException
	{
		String sRet;
		if(m_brFile != null)
		{   // Это чтение из файла
			m_intCurPos++;
			sRet = (m_brFile.readLine());
		} else
		{
			if(m_intCurPos >= m_asReps.size())
				sRet = null;
			else
				sRet = (m_asReps.get(m_intCurPos++));
		}
		return(sRet);
	}

	public void set_Data(String in_sRep)
	{
		m_asReps.clear();
		if(in_sRep != null)
		{
			StringTokenizer st = new StringTokenizer(in_sRep, "\n");

			int intCatAon = -1;
			while(st.hasMoreTokens())
			{
				m_asReps.add(st.nextToken());
			}
		}
	}
}











