package ru.DanilovAF.util;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Aleksandr.Danilov
 * Date: 03.02.14
 * Time: 8:56
 * Класс реализует сохранение слушателей и рассылку "событий"
 */
public class MyEventListener<E>
{
	public ArrayList<E> subScr = new ArrayList<E>();
	private int iClient = 0;

	public int addListener(E in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				subScr.add(in_l);
				iClient++;
			}
		}
		return(iClient);
	}
	public void removeListener(E in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				subScr.remove(in_l);
				iClient--;
			}
		}
	}
	public void clearListener()
	{
		synchronized (subScr)
		{
			subScr.clear();
			iClient = 0;
		}
	}

	public int getiClient()
	{
		int iRet = 0;
		synchronized (subScr)
		{
			iRet = iClient;
		}
		return iRet;
	}
//	public void fireEventCall(E msg)
//	{
//		synchronized (subScr)
//		{
//			for(E l :subScr)
//			{
//				if(l != null)
//				{
////					l.pushMsg(msg);
//				}
//			}
//		}
//
//	}
}
