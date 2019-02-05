package ru.DanilovAF.util.data;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 24.07.12
 * Time: 14:50
 * Словарь для ItemData
 */
public interface DictData
{
	public HashMap<String, String>getDict();
	public int getColumnCount();
	public String getColumnName(int in_iCol);
//	public String remove(int in_iCol);

	public boolean isSortLeft();

	public int getSortCol();
	String getTableName();
	public void addField(String in_sName);
}
