package ru.DanilovAF.util.ThreadMSG;


import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 08.06.12
 * Time: 11:59
 * Класс реализует стек событий
 * В этот стек из другого потока прилетают события, которые мы отрабатываем
 *
 * Есть два варианта или события обрабатываются асинхронно в другом потоке
 * или просто копим сюда события и затем разом их выбираем.
 *
 * Сообщения, которые прилетают здесь могут быть еще у нескольких подписчиков, поэтому сообщения только на чтение.
 *
 */
//public class MsgStack<E> implements Runnable
public class MsgStack<E> extends SynThread
{
//	protected volatile LinkedList<E> msgStack = new LinkedList<E>();
	protected volatile ArrayList<E> msgStack = new ArrayList<E>();
	protected volatile Object oWait = new Object();	// Объект для организации wait

	/**
	 * Вызывается в некоем потоке и помещает в стек событие
	 * Затем вызывает пробуждение всех страждущих
	 * @param in_e
	 */
	public boolean pushMsg(E in_e)
	{
		boolean bRet = true;
		if(checkMes(in_e))	// Проверка на валидность
		{
			synchronized (msgStack)
			{
				msgStack.add(in_e);
			}
			synchronized (oWait)
			{
				oWait.notify();    // Сказали, что стек изменился
			}
		} else
		{
			bRet = false;
		}
		return(bRet);
	}

	/**
	 * Проверка сообщения на валидность
	 * @param in_e
	 */
	public boolean checkMes(E in_e)
	{
		return(true);
	}

	/**
	 * Вытащить следующее по порядку сообщение, в потоке обработки
	 * @return
	 */
	public E popMsg()
	{
		E eRet = null;
		synchronized (msgStack)
		{

			if(!msgStack.isEmpty())
			{
				eRet = msgStack.get(0);
				msgStack.remove(0);
			}
		}
		return eRet;
	}

	/**
	 * Получение всех данных из стека при синхронном выполнении
	 * Вызывать только если никто больше сообщения в стек писать не будет !!!
	 * @return
	 */
	public void popAllMsg()
	{
		E eRet = null;
		while(true)
		{
			boolean bLast = false;
			synchronized (msgStack)
			{

				if (!msgStack.isEmpty())
				{
					eRet = msgStack.get(0);
					msgStack.remove(0);
					if(msgStack.isEmpty())
						bLast = true;
				} else
				{
					break;
				}
			}
			if(bLast)
				on_lastMessage(eRet);
			else
				on_allMessage(eRet);
		}
	}

	/**
	 * При синхронной функции popAllMsg вызывается эта функция для переопределения получения последнего значения
	 * @param eRet
	 */
	public void on_lastMessage(E eRet)
	{

	}

	/**
	 * При синхронной функции popAllMsg вызывается эта функция для переопределения
	 * в потомках
	 * @param eRet
	 */
	public void on_allMessage(E eRet)
	{

	}

	public void clearStackMsg()
	{
		synchronized (msgStack)
		{
			msgStack.clear();
		}
	}

	/**
	 * Асинхронный Поток вычерпывания данных из стека событий
	 */
	@Override
	public void run()
	{
		doBeforeStart();
		setStart();
		while(isNeedWork())
		{
			E msg = null;
			while( (msg = popMsg()) != null)
			{
				if(!isNeedWork()) { break; }	// Выйдем без вычерпывания стека - пора остановиться
				onMsg(msg);
			}
			if(isNeedWork())
			{
				synchronized (oWait)
				{
					try
					{
						oWait.wait(1000);
					} catch (InterruptedException e)
					{
						e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
					}
				}
			}
		}
		doBeforeStop();
		setStop();
	}

	/**
	 * Переопределять эту функцию и на входе в другом потоке получим событие
	 * @param msg
	 */
	public void onMsg(E msg)
	{

	}

	/**
	 * Синхронная функция типа обратного вызова для получения этого сообщения, на тот случай если хочется работать синхронно,
	 * Но это происходит из потока приема событий, так что делайте выводы
	 * Если нагрузить эту функцию, то затормозится поток разбора событий
	 * @param hmMsg
	 */
	public void on_message(E hmMsg)
	{

	}
}
