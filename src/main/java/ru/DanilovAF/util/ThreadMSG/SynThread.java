package ru.DanilovAF.util.ThreadMSG;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.DanilovAF.util.util;

/**
 * Поток с синхронизацией
 * Фишка в том, что есть флаг, что работаем.
 * есть флаг, означающий надо работать или надо останавливаться.
 * и есть уведомление, что мы остановились и запустились
 *
 * Идея в том, чтобы автоматизировать синхронизацию по запуску потока, типа сказали
 * запускайся и синхронизируемся по запуску
 *
 * Если надо остановиться, то скаомандовали - стой и синхронизируемся по остановке потока.
 *
 * Задача поднять флаг, что надо работать, запустить поток и подождать его запуск в waitStartThread
 * При этом когда все стартанет и загрузится в другом потоке, надо сказать setStartOld
 *
 * Когда надо останавливаться - после остановки надо сказать setStopOld
 *
 * Вот стандартная схема функции RUN
 *
 *  setStart();	// Все поток пошел работать
 *
 *  while (isNeedWork())	// Цикл основной работы, пока нет команды останавливаться
 *  {
 *
 *  }
 *	setStop();	// Сказали что ну не работаем
 *
 *
 *  Чтобы запустить этот поток
 *  ap.startThisThread();	// Запустили поток
 *  ap.waitStartThread();	// Если надо - можно подождать запуска
 *
 *  Чтобы остановиться
 *  ap.doNotWork();	// Хватит работать
 *  ap.shutdown();	// Ждем остановки
 *
 */
public class SynThread implements Runnable
{
	private static final Logger log = LoggerFactory.getLogger(SynThread.class);

	protected volatile Boolean needWork = new Boolean(false);    // Надо работать, если не надо работать - поток должен останавливаться
	private volatile Boolean start = new Boolean(false);        // Запущен я или не запущен
	private static long timeSleep = 500;        // Время ожидания выхода из wait на новый цикл ожидания
	protected Thread thisThread = null;    // Текущий поток здесь

	/**
	 * Отвечает на вопрос надо ли работать
	 *
	 * @return
	 */
	public boolean isNeedWork()
	{
//		boolean bRet = false;
//		synchronized(needWork)
//		{
//			bRet = needWork;
//		}
//		return bRet;
		return needWork.booleanValue();
	}

	/**
	 * Говорит потоку давай поработаетм, но не запускает поток
	 */
	public void letsWork()
	{
		setNeedWork(true);
	}

	/**
	 * Говорит потоку ну хватит уже работать
	 */
	public void doNotWork()
	{
		setNeedWork(false);
	}

	public void stop()
	{
		thisThread.stop();
	}

	/**
	 * Говорит потоку надо или не надо работать
	 *
	 * @param needWork
	 */
	public void setNeedWork(boolean needWork)
	{
		synchronized(this.needWork)
		{
			this.needWork = needWork;
		}
	}
	/**
	 * Если в приложении надо сделать паузу на timeSleep и при этом контролировать недо ли работать, то надо
	 * вызвать этму функцию
	 * @return истина если надо продолжать работать
	 * @throws InterruptedException
	 */
	public boolean waitNeedWork() throws InterruptedException
	{
		synchronized (needWork)
		{
			needWork.wait(timeSleep);
		}
		return(isNeedWork());
	}
	/**
	 * Отвечает на вопрос стартанул поток или нет
	 * @return
	 */
	public boolean isStart()
	{
		return start.booleanValue();
	}
	/**
	 * Говорит всем, что я стартанул, вызывать когда все запущено и поток готов к выполнению задачи
	 * @return
	 */
	public void setStart()
	{
		setStart(true);
	}
	/**
	 * Говорит всем, что я остановился
	 * @return
	 */
	public void setStop()
	{
		setStart(false);
	}
	/**
	 * Переводит стстус потока в запущен/не запущен
	 * @param inBStart
	 */
	private void setStart(boolean inBStart)
	{
		synchronized(start)
		{
			start = inBStart;
		}
		synchronized(start)
		{
			start.notifyAll();
		}
	}
	/**
	 * Говорит - давай останавливайся уже и ждет завершения потока
	 */
	public void shutdown() throws InterruptedException
	{
		doNotWork();    // сказали останавливайся
		waitStopThread();

	}

	public void waitStopThread() throws InterruptedException
	{
		while(isStart())	// Ждем, пока не остановимся
		{
			try
			{
				if(log.isInfoEnabled()) { log.info("Ждем закрытия потока! ---" ); }
				synchronized (start)
				{
					start.wait(timeSleep);
				}
			} catch(InterruptedException e)
			{
				log.error(util.stackTrace(e));
				throw e;	// Подбросим выше - пусть разбирается использующий класс
//				break;
			}
		}
	}

	/**
	 * Ждет пока поток запустится и будет готов к выполнению задачи
	 */
	public void waitStartThread() throws InterruptedException
	{
		while(isNeedWork())	// Пока надо работать
		{
			if(!isStart())	// Пока не стартанули
			{
				try
				{
					if(log.isInfoEnabled()) { log.info("Ждем запуска потока! +++" ); }
					synchronized (start)
					{
						start.wait(timeSleep);
					}
				} catch (InterruptedException e)
				{
					log.error(util.stackTrace(e));
					throw e;	// Подбросим выше - пусть разбирается использующий класс
				}
			} else
			{
				break;
			}
		}
	}
	@Override
	public void run()
	{
		doBeforeStart();
		setStart();	// Сказали, то запустились
		if(log.isInfoEnabled()) { log.info("Внимание не переопределили функцию запуска потора RUN! +++" ); }
		try
		{
			Thread.sleep(timeSleep);
		} catch(InterruptedException e)
		{
			log.error(util.stackTrace(e));
		}
		doBeforeStop();
		setStop();	// Сказали, что остановились, при этом флаг, что надо работать остался взведенным
	}

	/**
	 * Переопределить этот метод, если от нас скрыт RUN и надо что-то сделать перед остановкой потока
	 */
	protected void doBeforeStop()
	{

	}

	/**
	 * Переопределить этот метод, если от нас скрыт RUN и надо что-то сделать перед запуском потока
	 */
	protected void doBeforeStart()
	{

	}

	/**
	 * Запускает поток
	 * @return
	 */
	public Thread startThisThread()
	{
		// Запуск выдачи указанного лога в отдельном потоке
		letsWork();	// Давай поработаем
		thisThread = new Thread(this);
		thisThread.start();
		return (thisThread);
	}
}
