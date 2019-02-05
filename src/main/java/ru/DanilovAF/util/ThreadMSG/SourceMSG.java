package ru.DanilovAF.util.ThreadMSG;

import java.util.LinkedList;

/**
 * Created by aleksandr.danilov on 16.11.2017.
 * Класс для коллекции подписчиков
 * Пеодписчики собираются, чтобы получать какие-либо сообщения
 * Этот класс является источником этих событий - сохраняет в себе всех подписчиков
 * и Рассылает им сообщения
 *
 * При этом надо понимать, что сообщения состоят везде из одного объекта, т.е. нельзя ничего делать с сообщениями в стеке
 * Можно только взять и протичать и удалить из стека
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
		{   // Необходимо разослать всем подписчикам
			synchronized (subScr)
			{
				// Вызвать каллбак события в этих классах это синхронная функция, под нее лучше не писать, т.к.
				// замедляет поток разбора сообщений
				for(MsgStack<Mes> l : subScr)
				{
					boolean flagFiltr = checkFilter(l, hmMsg);
					if(flagFiltr)
					{
						l.on_message(hmMsg);
					}
				}
				// Асинхронное послание
				for(MsgStack<Mes> l : subScr)
				{
						// Проверим фильтр этого подписчика, если есть фильтр - надо фильтровать, если нет фильтра - надо отправить так
						// Фильтр двухступенчатый, сначала по ключу, затем по значению ключа
						boolean flagFiltr = checkFilter(l, hmMsg);
						if(flagFiltr)
						{
							l.pushMsg(hmMsg);	// В этом будит удаленный обработчик
						}

				}
			}
			// Разбудим всех подписчиков
			synchronized (this)
			{
				this.notifyAll();
			}
		}

	}

	/**
	 * Если нужен фильтр по событиям, то эту функцию надо переписать
	 * @param l
	 * @param hmMsg
	 * @return
	 */
	private boolean checkFilter(MsgStack<Mes> l, Mes hmMsg)
	{
		return true;
	}
}
