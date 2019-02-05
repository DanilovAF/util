package ru.DanilovAF.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aleksandr.danilov on 16.01.2018.
 * Для разбора опций
 *
 * содержит мап на название опции - значение - ввиде объекта
 * В качестве объекта будет или строка или массив
 *
 * опции всталять с двоеточием
 *
 */
public class Opt extends ArrayList<String>	// Значения опций
{
//	ArrayList<String> alFiles = new ArrayList<String>();			// параметры без опций
	HashMap<String, ArrayList<String>> hsOpt = new HashMap<String, ArrayList<String>>();	//  название опции и ее значение. может быть в качестве значения ArrayList<String>
	HashSet<String>	params = new HashSet<String>();// названия опций

	String vmDelimeter = ",";

	public void addParamName(String sParam)
	{
		params.add(sParam);
	}
	public boolean containsKey(String sKey)
	{
		return (hsOpt.containsKey(sKey));
	}

	public String getOneVal(String sKey)
	{
		if(!hsOpt.containsKey(sKey))
			return "";
		if(hsOpt.get(sKey).isEmpty())
			return "";

		return (hsOpt.get(sKey).get(0));
	}

	public ArrayList<String> getVals(String sKey)
	{
		if(!hsOpt.containsKey(sKey))
			return new ArrayList<String>();

		return (hsOpt.get(sKey));
	}

	public void parseOpt(String [] args)
	{
		for(String sParam : args)
		{
			if(sParam != null && !sParam.isEmpty()) {
				boolean bFlagFinde = false;
				// Разбор параметров
				for (String sP : params) {
					if (sParam.toLowerCase().indexOf(sP) == 0)    // Если начинается с этого то это параметр, иначе файл
					{
						bFlagFinde = true;
						if (sParam.length() > sP.length()) {
							String sVal = sParam.substring(sP.length());
							// Массив значений
							ArrayList<String> hsBuf = new ArrayList<String>();
							for (String sV : sVal.split(vmDelimeter)) {
								hsBuf.add(sV);
							}
							hsOpt.put(sP, hsBuf);
						} else {    // Просто опция
							hsOpt.put(sP, new ArrayList<String>());
						}
					}
				}
				if (!bFlagFinde) {    // Если это не опция значит просто значение - его добвим сюда
//				alFiles.add(sParam);
					add(sParam);
				}
			}
		}
	}



}
