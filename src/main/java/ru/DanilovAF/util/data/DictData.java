package ru.DanilovAF.util.data;

import ru.DanilovAF.util.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 24.07.12
 * Time: 15:45
 * Описание записей. Работать так:
 * Объявляем статическое поле в классе
 * private static DictData dict;
 *
 * Объявляем название полей
 * static public String _VL_TEL = "tel";
 * static public String _VL_COUNT_CALL = "vl.count";
 * static public String _VL_STAT = "vl.stat";
 *
 * Делаем в классе статический блок заполнения словаря
 * static
 * {
 * 	dict = new DictData();
 * 	dict.addField(_VL_TEL);
 * 	dict.addField(_VL_COUNT_CALL);
 * 	dict.addField(_VL_STAT);
 * }
 *
 * Далее по тексту программы можно делать следюущуу конструкцию:
 *
 * ItemData activVlan = new ItemData(dict);
 * activVlan.set(_VL_TEL, sBuf);
 *
 */
// public class DictData  implements DictData
public class DictData extends HashMap<String, String>
{
//	public HashMap<String, String> dict = new HashMap<String, String>();

	public int sortCol = 0; // Номер колонки по которому сортировать
	public boolean sortLeft = false; // Сортировать как числа
	public int iCountFild = 0;  // Кол-во полей - увеличивается при добавлении очередного поля
	private String tableName;

	public DictData() {
	}

	/**
	 * Получить словарь по выборке
	 * @param ps
	 * @throws SQLException
	 */
	public DictData(ResultSet ps) throws SQLException {
		ResultSetMetaData rsm = ps.getMetaData();

		int iCount = rsm.getColumnCount();
		for (int i = 1; i <= iCount; i++) {
			addField(rsm.getColumnName(i));
			if(i == 1)
				setTableName(rsm.getTableName(i));
		}
	}

	@Override
	public String toString() {
		StringBuffer sp = new StringBuffer();
		for(int i = 0; i < getColumnCount(); i++)
		{
			sp.append(getColumnName(i)).append("^");
		}
		sp.delete(sp.length() - 1, sp.length());
		return sp.toString();
	}

	public void setSortLeft(boolean sortLeft)
	{
		this.sortLeft = sortLeft;
	}

	public void setSortCol(int sortCol)
	{
		this.sortCol = sortCol;
	}

	public void addField(String in_sName)
	{
		put("" + iCountFild, in_sName);
		put(in_sName, "" + iCountFild);
		iCountFild++;
	}

	public void addFieldSyn(String in_sName, int iPos)
	{
		if(in_sName != null && iPos <= iCountFild)
		{
//			dict.put("" + iPos, in_sName);
			put(in_sName, "" + iPos);
		}
	}

	public HashMap<String, String> getDict()
	{
		return this;
	}
	public int getColumnCount()
	{
		return size()/2;
	}
	public String getColumnName(int in_iCol)
	{
		return get("" + in_iCol);
	}

//	@Override
//	public String remove(int in_iCol)
//	{
//		String sRet = getColumnName(in_iCol);
//
//		return sRet;
//	}

	public boolean isSortLeft()
	{
		return sortLeft;
	}

	public int getSortCol()
	{
		return sortCol;
	}

	public String getTableName()
	{
		return tableName;
	}

	public int getColumnNum(String tel_sost)
	{
		int iRet = (int) util.mcn(get(tel_sost), '.');
		return iRet;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	public boolean isContainField(String sField)
	{
		return containsKey(sField);
	}
}
