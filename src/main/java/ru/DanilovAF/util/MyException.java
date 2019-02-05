package ru.DanilovAF.util;

/**
 * Created by aleksandr.danilov on 16.11.2017.
 * Мой класс ошибки  - добавлен код ошибки  - мервая или нет
 *
 */
public class MyException extends Exception
{
	/**
	 * После отстоя возможно команда исполнится
	 */
	static public int LEV_NO_FATAL = 1;
	/**
	 * Данная команда никогда не исполнится
	 */
	static public int LEV_FATAL = 0;
	/**
	 * Данный процесс никогда не исполнит эту ошибку
	 */
	static public int LEV_PROCESS_CANT_EXEC = 2;
	/**
	 * Данный команда на станцию не посылалась, т.к. требуемое уже достигнуто
	 */
	static public int LEV_DO_NOFING = 3;

	/**
	 * этот уровень ошибки будет означать, что команда закончилась ошибкой, но персоналу эту ошибку показывать не надо.
	 * Типа номер сейчас занят и его не измерить - кто промил это измерение увидит, что номер занят, но в список ошибк,
	 * которые надо отрабатывать эта команда не попадет. Типа ошибка ну и ладно... Орицательный результат - тоже результат.
	 */
	static public int LEV_FATAL_NO_ERROR = 4;
	private String area = "";	// Область к которой относится ошибка если захотим нумеровать ошибки

	protected int m_intLavel = -1;

	/**
	 * Сообщение ошибки о том, что переменная не инициализирована в случае если функция вытащена в базовый класс
	 * а в текущем потомке она не переопределена.
	 */
	public static String varNotInit = "Переменная не инициализирована в этом рапорте.";

	public void setM_intLavel(int m_intLavel)
	{
		this.m_intLavel = m_intLavel;
	}
	/**
	 * Можно статически задать эту величину, она будет выводиться во всех ошибках как преффикс.
	 */
	private static String sPreffix = "";

	public static void setSPreffix(String in_sPreffix)
	{
		sPreffix = in_sPreffix;
	}

	/**
	 * Код ошибки. Принять следующие коды ошибки:
	 * 1 - [1.Local Error:EAtTcpControl SocketSendBuf.2:Нет соединения!]
	 * 2 - Поступила команда на отклюение местной связи (ID = -1), но номер (8182642680) уже отключен.
	 * 3 - Поступила команда на включение местной связи но номер уже включен
	 * 4 - pilot break user - эту ошибку возвращает терминал при нажатии кнопки прервать пилот
	 */
	protected int m_intCode = -1;

	public MyException() {
		super();    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * Коструктор для ошибки на АТС.
	 * @param message Сообщение ошибки.
	 * @param in_intCode Код ошибки для более простого анализа ошибки - коды не должны пересекаться.
	 * @param in_intLavel Уровень ошибки, определены в этом классе 3 уровня   LEV_NO_FATAL, LEV_FATAL, LEV_PROCESS_CANT_EXEC, LEV_DO_NOFING
	 */
	public MyException(String message, int in_intCode, int in_intLavel)
	{
		super(sPreffix + message);
		m_intCode = in_intCode;
		m_intLavel = in_intLavel;
	}

	public MyException(String message, int m_intCode, int m_intLavel, String area)
	{
		super(message);
		this.m_intCode = m_intCode;
		this.m_intLavel = m_intLavel;
		this.area = area;
	}

	public int getM_intLavel() {
		return m_intLavel;
	}

	public int getM_intCode() {
		return m_intCode;
	}
}
