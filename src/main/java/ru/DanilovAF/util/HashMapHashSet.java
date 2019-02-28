package ru.DanilovAF.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by aleksandr.danilov on 19.02.2019.
 */
public class HashMapHashSet<type> extends HashMap<type, HashSet<type>> {
	// Надо добавить функции добавления в коллекцию, при этом, если еть ключ - добавить значение, если не т ключа - создать его

	/**
	 * Добавление значения в любом случае
	 * @param key
	 * @param val
	 * @return
	 */
	public HashMapHashSet<type> addVm(type key, type val)
	{
		if(containsKey(key)) {
			get(key).add(val);
		} else
		{
			HashSet<type> buf = new HashSet<type>();
			buf.add(val);
			put(key, buf);
		}
		return this;
	}

	/**
	 * Поиск значениея в sete
	 * @param key
	 * @param val
	 * @return
	 */
	public boolean containsVal(type key, type val) {
		boolean bRet = false;
		if(containsKey(key)) {
			bRet = get(key).contains(val);
		}
		return bRet;
	}
}
