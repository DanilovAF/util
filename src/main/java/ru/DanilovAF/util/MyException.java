package ru.DanilovAF.util;

/**
 * Created by aleksandr.danilov on 16.11.2017.
 * ��� ����� ������  - �������� ��� ������  - ������ ��� ���
 *
 */
public class MyException extends Exception
{
	/**
	 * ����� ������ �������� ������� ����������
	 */
	static public int LEV_NO_FATAL = 1;
	/**
	 * ������ ������� ������� �� ����������
	 */
	static public int LEV_FATAL = 0;
	/**
	 * ������ ������� ������� �� �������� ��� ������
	 */
	static public int LEV_PROCESS_CANT_EXEC = 2;
	/**
	 * ������ ������� �� ������� �� ����������, �.�. ��������� ��� ����������
	 */
	static public int LEV_DO_NOFING = 3;

	/**
	 * ���� ������� ������ ����� ��������, ��� ������� ����������� �������, �� ��������� ��� ������ ���������� �� ����.
	 * ���� ����� ������ ����� � ��� �� �������� - ��� ������ ��� ��������� ������, ��� ����� �����, �� � ������ �����,
	 * ������� ���� ������������ ��� ������� �� �������. ���� ������ �� � �����... ������������ ��������� - ���� ���������.
	 */
	static public int LEV_FATAL_NO_ERROR = 4;
	private String area = "";	// ������� � ������� ��������� ������ ���� ������� ���������� ������

	protected int m_intLavel = -1;

	/**
	 * ��������� ������ � ���, ��� ���������� �� ���������������� � ������ ���� ������� �������� � ������� �����
	 * � � ������� ������� ��� �� ��������������.
	 */
	public static String varNotInit = "���������� �� ���������������� � ���� �������.";

	public void setM_intLavel(int m_intLavel)
	{
		this.m_intLavel = m_intLavel;
	}
	/**
	 * ����� ���������� ������ ��� ��������, ��� ����� ���������� �� ���� ������� ��� ��������.
	 */
	private static String sPreffix = "";

	public static void setSPreffix(String in_sPreffix)
	{
		sPreffix = in_sPreffix;
	}

	/**
	 * ��� ������. ������� ��������� ���� ������:
	 * 1 - [1.Local Error:EAtTcpControl SocketSendBuf.2:��� ����������!]
	 * 2 - ��������� ������� �� ��������� ������� ����� (ID = -1), �� ����� (8182642680) ��� ��������.
	 * 3 - ��������� ������� �� ��������� ������� ����� �� ����� ��� �������
	 * 4 - pilot break user - ��� ������ ���������� �������� ��� ������� ������ �������� �����
	 */
	protected int m_intCode = -1;

	public MyException() {
		super();    //To change body of overridden methods use File | Settings | File Templates.
	}

	/**
	 * ���������� ��� ������ �� ���.
	 * @param message ��������� ������.
	 * @param in_intCode ��� ������ ��� ����� �������� ������� ������ - ���� �� ������ ������������.
	 * @param in_intLavel ������� ������, ���������� � ���� ������ 3 ������   LEV_NO_FATAL, LEV_FATAL, LEV_PROCESS_CANT_EXEC, LEV_DO_NOFING
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
