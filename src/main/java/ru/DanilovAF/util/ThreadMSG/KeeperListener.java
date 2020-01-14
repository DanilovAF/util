package ru.DanilovAF.util.ThreadMSG;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: DanilovAF
 * Date: 23.09.13
 * Time: 14:59
 * Хранитель слушателей
 */
public class KeeperListener<E>
{
	private int iClient = 0;
	ArrayList<MsgStack<E>> subScr = new ArrayList<MsgStack<E>>();

	public int addCallListenerS(ArrayList<MsgStack<E>> in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				for(MsgStack<E> ms: in_l)
				{
					subScr.add(ms);
				}
			}
			iClient++;
		}
		return(iClient);
	}
	public int addCallListener(KeeperListener<E> in_kl)
	{
		if(in_kl != null)
		{
			addCallListenerS(in_kl.getSubScr());
			iClient++;
		}
		return(iClient);
	}
	public int addCallListener(MsgStack<E> in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				subScr.add(in_l);
			}
			iClient++;
		}
		return(iClient);
	}
	public void removeCallListenerS(ArrayList<MsgStack<E>> in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				for(MsgStack<E> ms: in_l)
				{
					subScr.remove(ms);
				}
			}
			iClient--;
		}
	}
	public int removeCallListener(KeeperListener<E> in_kl)
	{
		if(in_kl != null)
		{
			removeCallListenerS(in_kl.getSubScr());
			iClient--;
		}
		return(iClient);
	}

	public void removeCallListener(MsgStack<E> in_l)
	{
		if(in_l != null)
		{
			synchronized (subScr)
			{
				subScr.remove(in_l);
			}
			iClient--;
		}
	}
	public void fireEventCall(E msg)
	{
		synchronized (subScr)
		{
			for(MsgStack<E> l :subScr)
			{
				if(l != null)
				{
					l.pushMsg(msg);
				}
			}
		}
	}

	public ArrayList<MsgStack<E>> getSubScr()
	{
		return subScr;
	}
}
