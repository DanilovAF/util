package ru.DanilovAF.util.ThreadMSG;

import java.util.LinkedList;

/**
 * Created by aleksandr.danilov on 16.11.2017.
 * ����� ��� ��������� �����������
 * ����������� ����������, ����� �������� �����-���� ���������
 * ���� ����� �������� ���������� ���� ������� - ��������� � ���� ���� �����������
 * � ��������� �� ���������
 *
 * ��� ���� ���� ��������, ��� ��������� ������� ����� �� ������ �������, �.�. ������ ������ ������ � ����������� � �����
 * ����� ������ ����� � ��������� � ������� �� �����
 *
 */
public class SourceMSG<Mes>
{
	private LinkedList<MsgStack<Mes>> subScr = new LinkedList<MsgStack<Mes>>();
	private int iClient = 0;

	public int addMsgListener(MsgStack<Mes> in_l)
	{
		synchronized (subScr)
		{
			subScr.add(in_l);
			iClient++;
		}
		return(iClient);
	}
	public void removeMsgListener(MsgStack<Mes> in_l)
	{
		synchronized (subScr)
		{
			subScr.remove(in_l);
			iClient--;
		}
	}

	public void removeAllMsgListener()
	{
		synchronized (subScr)
		{
			subScr.clear();
			iClient = 0;
		}
	}

	public void sendMSG(Mes hmMsg)
	{
		if(hmMsg != null)
		{   // ���������� ��������� ���� �����������
			synchronized (subScr)
			{
				// ������� ������� ������� � ���� ������� ��� ���������� �������, ��� ��� ����� �� ������, �.�.
				// ��������� ����� ������� ���������
				for(MsgStack<Mes> l : subScr)
				{
					boolean flagFiltr = checkFilter(l, hmMsg);
					if(flagFiltr)
					{
						l.on_message(hmMsg);
					}
				}
				// ����������� ��������
				for(MsgStack<Mes> l : subScr)
				{
						// �������� ������ ����� ����������, ���� ���� ������ - ���� �����������, ���� ��� ������� - ���� ��������� ���
						// ������ ���������������, ������� �� �����, ����� �� �������� �����
						boolean flagFiltr = checkFilter(l, hmMsg);
						if(flagFiltr)
						{
							l.pushMsg(hmMsg);	// � ���� ����� ��������� ����������
						}

				}
			}
			// �������� ���� �����������
			synchronized (this)
			{
				this.notifyAll();
			}
		}

	}

	/**
	 * ���� ����� ������ �� ��������, �� ��� ������� ���� ����������
	 * @param l
	 * @param hmMsg
	 * @return
	 */
	private boolean checkFilter(MsgStack<Mes> l, Mes hmMsg)
	{
		return true;
	}
}
