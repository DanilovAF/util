package ru.DanilovAF.util.ThreadMSG;

/**
 * Created by aleksandr.danilov on 16.11.2017.
 * ѕоток "наследует" свойства SynThread и SourceMSG
 *
 * Ќе понимаю дл€ чего € это сделал - оставлено дл€ совсместимости.
 */
public class SynThreadSourceMSG<Mes> extends SynThread
{
	protected SourceMSG<Mes> listners = new SourceMSG<Mes>();

	public int addMsgListener(MsgStack<Mes> in_l)
	{
		return(listners.addMsgListener(in_l));
	}
	public void removeMsgListener(MsgStack<Mes> in_l)
	{
		listners.removeMsgListener(in_l);
	}

	public void sendMSG(Mes hmMsg)
	{
		listners.sendMSG(hmMsg);
	}

	private boolean checkFilter(MsgStack<Mes> l, Mes hmMsg)
	{
		return false;
	}

	public void Wait() throws InterruptedException
	{
		synchronized(listners)
		{
			listners.wait();
		}
	}
}
