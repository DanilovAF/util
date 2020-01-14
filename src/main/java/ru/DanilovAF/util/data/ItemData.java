package ru.DanilovAF.util.data;

import ru.DanilovAF.util.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 24.07.12
 * Time: 15:01
 * Описание одной записи
 */
public class ItemData implements Comparable<ItemData>
{
//	public static final String VM = "\u00fd";
	public static final String VM = "\n";
	public ArrayList<String> item = new ArrayList<String>();
	protected DictData dict;

	public ItemData()
	{
	}

	public ItemData(DictData dict)
	{
		this.dict = dict;
	}

	public ItemData(ItemData in_Item)
	{
		if(in_Item != null)
		{
			dict = in_Item.getDictData();
			int i = 0;
			for(String sAm: in_Item.item)
			{
				item.add(sAm);
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer sp = new StringBuffer();
		for(String s: item)
		{
			if(s == null)
				sp.append("^");
			else
				sp.append(util.replace(s,VM, "[")).append("^");
		}
		sp.delete(sp.length() - 1, sp.length());
		return sp.toString();
	}

	@Override
	public int compareTo(ItemData o)
	{
		int iRet = -1;
		if(dict.isSortLeft())
		{   // Сравниваем как цифры
			double d1 =	util.mcn(get(dict.getSortCol()), '.');
			double d2 =	util.mcn(o.get(dict.getSortCol()), '.');
			if(d1 > d2)
			{
				iRet = 1;
			} else if(d1 == d2)
			{
				iRet = 0;
			}
		} else
		{
			iRet =  get(dict.getSortCol()).compareTo(o.get(dict.getSortCol()));
		}
		return iRet;
	}

	/**
	 * Получим названия атрибутов, которые отличаются в записях
	 * @param in_item
	 * @return
	 */
	public boolean getChangeAM(ItemData in_item, ArrayList<String> alUpdateAm)
	{	// Если какие-то поля изменились по сравн со старой записью - обновить !!!!!
		boolean bRet = false;
		if(alUpdateAm == null)
		{
			alUpdateAm = new ArrayList<String>();
		}
		int iLen = size();
		if(in_item.size() > iLen) { iLen = in_item.size(); }
		for(int i = 0; i < iLen; i++)
		{
			if((get(i) == null && in_item.get(i) != null) || (in_item.get(i) == null && get(i) != null) || (get(i) != null && get(i).compareToIgnoreCase(in_item.get(i)) != 0))
			{
				String sName = getDict().get("" + i);
				alUpdateAm.add(sName);
				bRet = true;
			}
		}
		return(bRet);
	}

	/**
	 * Получить список не пустых атрибутов в записи
	 * @param alUpdateAm
	 * @return
	 */
	public ArrayList<String> getNotEmptyAm(ArrayList<String> alUpdateAm)
	{
		if(alUpdateAm == null)
		{
			alUpdateAm = new ArrayList<String>();
		}
		for(int i = 0; i < size(); i++)
		{
			if(get(i) != null)
			{
				String sName = getDict().get("" + i);
				alUpdateAm.add(sName);
			}
		}
		return(alUpdateAm);
	}

	public String delete(int iIndex)
	{
		String sRet = item.get(iIndex);
		item.remove(iIndex);
		return sRet;
	}

//	public String deleteColumn(int iIndex)
//	{
//		return dict.remove(iIndex);
//	}

	public HashMap<String, String> getDict()
	{
		return dict.getDict();
	}

	public int getColumnCount()
	{
		return dict.getColumnCount();
	}

	public String getColumnName(int in_iCol)
	{
		return dict.getColumnName(in_iCol);
	}

	public String get(int index)
	{
		return item.get(index);
	}

	/**
	 * Нумерация VM с нулевого
	 * @param in_sKey
	 * @param iVm
	 * @return
	 */
	public String getVm(String in_sKey, int iVm) {
		String sRet = get(in_sKey);
		if(sRet != null) {
			String[] dimRet = sRet.split(VM);
			if (iVm == -1) {
				sRet = dimRet[dimRet.length - 1];
			} else if (iVm < dimRet.length) {
				sRet = dimRet[iVm];
			} else {
				sRet = null;
			}
		}
		return sRet;
	}

	public String get(String in_sKey)
	{
		int index = -1;
		String sRet = null;
		if(util.mcnS(in_sKey, '.').compareTo(in_sKey) == 0)
		{   // Запрос по номеру
			index = ((int) util.mcn(in_sKey, '.'));
		} else
		{   // Запрос по названию атрибута
			if(dict != null && dict.getDict() != null && dict.getDict().containsKey(in_sKey))
			{
				index = ((int) util.mcn(dict.getDict().get(in_sKey), '.'));
			}
		}
		if(index != -1)
		{
			if(item.size() > index)
			{
				sRet = item.get(index);
			}
		}
		return sRet;
	}

	/**
	 * Получение очередной записи в себя
	 * @param rs
	 *
	 * Пример кода для использования этой финкции
	 *
	 *         DictData dd = new DictData(rs);
	 *         ItemData it = new ItemData(dd);
	 *         while (rs.next()) {
	 *         	 it.setCurItem(rs);
	 *         }
	 *
	 * @return
	 * @throws SQLException
	 */
	public ItemData setCurItem(ResultSet rs) throws SQLException {
		String sBuf = "";
		clearMe();
		for(int i = 1; i <= getColumnCount(); i++)
		{
			sBuf = rs.getString(i);
			set(i - 1, sBuf);
		}
		return this;
	}

	/**
	 * Очистить все данные
	 */
	private void clearMe() {
		item.clear();
	}

	public ItemData set(int in_iNumFiled, String in_sVal)
	{
		boolean bRte = true;
		// Запрос по номеру
		if(in_iNumFiled >= item.size())
		{
			for(int i = item.size(); i <= in_iNumFiled; i++)
			{
				item.add("");
			}
		}
		item.set(in_iNumFiled, in_sVal);
		return this;
	}
	public ItemData add(int in_iNumFiled, String in_sVal)
	{
		set(in_iNumFiled, in_sVal);
		return this;
	}
	public ItemData add(String in_sKey, String in_sVal)
	{
		set(in_sKey, in_sVal);
		return this;
	}

	/**
	 * Добавить всегда, если даже в словаре нет поля - поле будет добавлено
	 * @param in_sKey
	 * @param in_sVal
	 * @return
	 */
	public ItemData addAlways(String in_sKey, String in_sVal)
	{
		if(in_sKey != null && !in_sKey.isEmpty()) {
			if(dict == null)
				dict = new DictData();
			if (!dict.getDict().containsKey(in_sKey)) {
				dict.addField(in_sKey);
			}
			set(in_sKey, in_sVal);
		}
		return this;
	}

	public ItemData addVm(int in_iNumFiled, String in_sVal)
	{
		if(get(in_iNumFiled) != null)
		{
			in_sVal = get(in_iNumFiled) + VM + in_sVal;
		}
		set(in_iNumFiled, in_sVal);
		return this;
	}
	public ItemData addVm(String in_sKey, String in_sVal)
	{
		if(get(in_sKey) != null)
		{
			in_sVal = get(in_sKey) + VM + in_sVal;
		}
		set(in_sKey, in_sVal);
		return this;
	}

	public ItemData set(String in_sKey, String in_sVal)
	{
		boolean bRte = true;
		if(util.mcnS(in_sKey, '.').compareTo(in_sKey) == 0)
		{   // Запрос по номеру
			item.set((int) util.mcn(in_sKey, '.'), in_sVal);
		} else
		{   // Запрос по названию атрибута
			if(dict.getDict().containsKey(in_sKey))
			{
				// Если вставляем в индекс больше чем сейчас есть массив, то расширом его
				int index = (int) util.mcn(dict.getDict().get(in_sKey), '.');
				if(index >= item.size())
				{
					for(int i = item.size(); i <= index; i++)
					{
						item.add(null);
					}
				}
				item.set(index, in_sVal);
			} else
			{
				bRte = false;
			}
		}
		return this;
	}

	// Создает запись с указанным словаерем и заполняет ее атрибутами из строки - разделитель ^
	public static ItemData newItem(DictData dict, String sIt)
	{
		ItemData iRet = new ItemData(dict);
		int i = 0;
		for(String sField : sIt.split("^"))
		{
			iRet.set(i++, sField);
		}
		return(iRet);
	}
	// Создает запись с указанным словаерем и заполняет ее атрибутами из строки - разделитель ^
	public static ItemData newItemAM(DictData dict, String sIt, String sDelim)
	{
		ItemData iRet = new ItemData(dict);
		int i = 0;
		for(String sField : sIt.split(sDelim))
		{
			iRet.set(i++, sField);
		}
		return(iRet);
	}

	public void setDict(DictData dd)
	{
		dict = dd;
	}

	public String createSqlInsert(String sTableName, ArrayList<String> sFieldNames)
	{
		StringBuffer sSql = new StringBuffer();
		if(sFieldNames != null && !sFieldNames.isEmpty())
		{
			sSql.append("insert into ").append(sTableName).append(" (");
			int i = 0;
			for(String sFieldName : sFieldNames)
			{
				if(get(sFieldName) != null)
				{
					if(i++ != 0)
					{
						sSql.append(", ");
					}
					sSql.append(sFieldName);
				}
			}
			sSql.append(") values (");
			i = 0;
			for(String sFieldName : sFieldNames)
			{
				String sVal = get(sFieldName);
				if(sVal != null)
				{
					if(i++ != 0)
					{
						sSql.append(", ");
					}
					sSql.append("'").append(sVal).append("'");
				}
			}
			sSql.append(")");
		}
		return sSql.toString();
	}

	public String createSqlUpdate(String sTableName, String sIdName, ArrayList<String> sFieldNames)
	{
		StringBuffer sSql = new StringBuffer();
		if(sFieldNames != null && !sFieldNames.isEmpty())
		{
			sSql.append("update ").append(sTableName).append(" SET ");
			int i = 0;
			for(String sFieldName: sFieldNames)
			{
				if(i++ != 0)
				{
					sSql.append(", ");
				}
				String sVal = get(sFieldName);
//				double l = util.mcn(sVal, '.');
//				String sL = util.trimR("" + l, "0.");
//				if(sVal.compareTo(sL) == 0 || sVal.compareTo("" + l) == 0)
//				{
//					sSql.append(sFieldName).append("=").append(get(sFieldName));
//				} else
				if(sVal == null)
				{
					sSql.append(sFieldName).append("=null");
				} else
				{
					sSql.append(sFieldName).append("='").append(get(sFieldName)).append("'");
				}
			}
//			String sVal = get(sIdName);
//			double l = util.mcn(sVal, '.');
//			String sL = util.trimR("" + l, "0.");
//			if(sVal.compareTo(sL) == 0 || sVal.compareTo("" + l) == 0)
//			{
//				sSql.append(" where ").append(sIdName).append("=").append(get(sIdName));
//			} else
			{
				sSql.append(" where ").append(sIdName).append("='").append(get(sIdName)).append("'");
			}
		}
		return (sSql.toString());
	}

	public DictData getDictData()
	{
		return dict;
	}

	public int size()
	{
		return item.size();
	}

	public String getTableName()
	{
		return dict.getTableName();
	}
}
