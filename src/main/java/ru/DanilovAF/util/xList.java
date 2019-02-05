package ru.DanilovAF.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 15.01.2010
 * Time: 10:47:01
 * Класс описывает связный список узлов. Причем навигация в нем же самом
 */
public class xList<typeThis>
{
	public xList()
	{
	}

	private int y = 0;

	public int getY()
	{
		return y;
	}

	/**
	 * Next Node следующий узел
	 */
	public typeThis _nn = null;
	public typeThis _pn = null;


	public static void main(String[] args)
	{
//		xList y2 = new xList();
//		xList y3 = new xList();
//		xList y4 = new xList();
//		xList l = y2;
//		l = l.toHead(y3);
//		l = l.toHead(y4);
//		l = l.toHead(l);
//		System.out.println(l);
//		l = l.getFirst();
//		l.clear();
//		System.out.println(l);
	}

	public boolean hasNext()
	{
	    return(_nn != null);
	}
	public boolean hasPrev()
	{
	    return(_pn != null);
	}
	public typeThis next()
	{
		return(_nn);
	}

	public typeThis prev()
	{
		return(_pn);
	}
	public typeThis getFirst()
	{
		typeThis This = (typeThis)this;	// Опустимся в конец текущего списка
		while(((xList<typeThis>)This).hasPrev())
		{
			This = ((xList<typeThis>)This).prev();
			if(This == this)
			{
				This = ((xList<typeThis>)This).next();
				break;
			}
		}
		return(This);
	}
	public typeThis getLast()
	{
		typeThis This = (typeThis)this;	// Опустимся в конец текущего списка
		while(((xList<typeThis>)This)._nn != null)
		{
			This = ((xList<typeThis>)This).next();
			if(This == this)
			{
				This = ((xList<typeThis>)This).prev();
				break;
			}
		}
		return(This);
	}

	/**
	 * Делает переданный узел вершиной списка
	 * @param aThis новая вершина списка
	 * @return новую вершину
	 */
	private typeThis setPN(typeThis aThis)
	{
		typeThis xlRet;
		if(aThis == null)
		{
			xlRet = null;	// Будет возвращен пусто
		} else
		{
			typeThis This = getFirst();	// Уйдем в начало текущего списка
			typeThis NN = ((xList<typeThis>)aThis).getLast();	// уйдем в конец списка куда вставимся
			((xList<typeThis>)This)._pn = NN;
			((xList<typeThis>)NN)._nn = This;
			xlRet = NN;
		}
		return(xlRet);
	}

	/**
	 * Добавляет в конец текущего списка переданный, соответственно в начало переданного встает в хвост текущего списка
	 * @param aThis
	 * @return
	 */
	private typeThis setNN(typeThis aThis)
	{
		typeThis xlRet;
		if(aThis == null)
		{
			xlRet = null;	// Будет возвращен пусто
		} else
		{
			typeThis This = getLast();	// Опустимся в конец текущего списка
			typeThis NN = ((xList<typeThis>)aThis).getFirst();	// уйдем в начало вставляемого списка
			((xList<typeThis>)This)._nn = NN;
			((xList<typeThis>)NN)._pn = This;
			xlRet = NN;
		}
		return(xlRet);
	}

	/**
	 * Втать в начало этого списка, т.е. вставлемый узел становится первым.
	 * @param aThis что вставляем
	 * @return начало списка (первый элемент)
	 */
	public typeThis toHead(typeThis aThis)
	{
		return(setPN(aThis));
	}

	/**
	 * Вставить в конец списка данный элемент
	 * @param aThis что вставляем
	 * @return вернется, первая ссылка на то что вставили
	 */
	public typeThis toEnd(typeThis aThis)
	{
		return(setNN(aThis));
	}
	/**
	 * Оставляет только текущий узел с развязыванием всего списка
	 */
	public void clear()
	{
		typeThis This = getFirst();
		typeThis bak = This;
		while(((xList<typeThis>)This)._nn != null)
		{
			bak = This;
			This = ((xList<typeThis>)This).next();
			((xList<typeThis>)bak)._pn = null;
			((xList<typeThis>)bak)._nn = null;
		}
		((xList<typeThis>)bak)._pn = null;
	}
//	@Override
//	public String toString()
//	{
//		StringBuffer sb = new StringBuffer();
//		xList<typeThis> xl = this;
//		do
//		{
//			sb.append(xl.getY());
//		} while(((xl = xl.next()) != null) && (xl != this));
//		return(sb.toString());
//	}
	/**
	 * Получает в виде параметризованного списка все элементы. Если переопределить эту функцию, то получим хорошо
	 * @return список всех элементов в массиве
	 */
	public ArrayList<typeThis> getList()
	{
		typeThis This = getFirst();
		typeThis bakThis = This;
		ArrayList<typeThis> lRet = new ArrayList<typeThis>();
		do
		{
			lRet.add((typeThis)This);
		} while(((This = ((xList<typeThis>)This).next()) != null) && (This != bakThis));
		return(lRet);
	}

}

