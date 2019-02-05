package ru.DanilovAF.util.xml;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 06.04.2010
 * Time: 16:44:02
 * Для хранения свойств в XML
 */
public class mProp	extends LinkedList<mProp.Twice>
{
	public String getVal(String in_sKey)
	{
		String sRet = null;
		for(int i = 0; i < size(); i++)
		{
			if(get(i).getSKey().compareTo(in_sKey) == 0)
			{
				sRet = get(i).getSVal();
				break;
			}
		}
		return(sRet);
	}

	public Twice get(String in_sKey)
	{
		Twice Ret = null;
		for(int i = 0; i < size(); i++)
		{
			if(get(i).getSKey().compareTo(in_sKey) == 0)
			{
				Ret = get(i);
				break;
			}
		}
		return(Ret);
	}

	public void addLast(String in_sKey, String in_sVal)
	{
		super.addLast(new Twice(in_sKey, in_sVal));	//To change body of overridden methods use File | Settings | File Templates.
	}

	public String getProp()
	{
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < size(); i++)
		{
			sb.append(" ");
			sb.append(get(i).getSKey());
			sb.append("=\"");
			sb.append(get(i).getSVal());
			sb.append("\"");
		}
		return(sb.toString());
	}

	public class Twice
	{
		private String sKey;
		private String sVal;

		public Twice(String sKey, String sVal)
		{
			this.sKey = sKey;
			this.sVal = sVal;
		}

		public String getSKey()
		{
			return sKey;
		}

		public void setSKey(String sKey)
		{
			this.sKey = sKey;
		}

		public String getSVal()
		{
			return sVal;
		}

		public void setSVal(String sVal)
		{
			this.sVal = sVal;
		}
	}
}
