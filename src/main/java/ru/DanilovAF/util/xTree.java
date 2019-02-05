package ru.DanilovAF.util;


import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 26.07.12
 * Time: 15:53
 * Класс для работы с деревьями
 */
public class xTree<typeOb> implements TreeNode, Comparable<xTree<typeOb>>
{
	public Vector<xTree> children = new Vector<xTree>();
	public xTree parent = null;
	public typeOb ob = null;
	public Boolean imloaded = new Boolean(false);

	// Разделитель, который вставляется между узлами при выводе
	public static String delimToString = "\n";

	public typeOb getOb()
	{
		return ob;
	}

	public ArrayList<xTree<typeOb>> getAllNodeInList()
	{
		ArrayList<xTree<typeOb>> alStack = new ArrayList<xTree<typeOb>>();
		ArrayList<xTree<typeOb>> alStackRet = new ArrayList<xTree<typeOb>>();
		alStack.add(this);
		while(alStack.size() > 0) {
			alStackRet.addAll(alStack);
			ArrayList<xTree<typeOb>> alStackBuf = new ArrayList<xTree<typeOb>>();
			// Получим следующий уровень в стек
			for (xTree<typeOb> n : alStack) {
				for (xTree<typeOb> nCh : n.getChildren()) {
					alStackBuf.add(nCh);
				}
			}
			alStack = alStackBuf;
		}
		return(alStackRet);
	}
	/**
	 * Получить максимальный уровень вложенности дерева без рекурсии
	 * @return
	 */
	public int getMaxLavel()
	{
		int iRet = 0;
		ArrayList<xTree<typeOb>> alStack = new ArrayList<xTree<typeOb>>();
		alStack.add(this);
		while(alStack.size() > 0) {
			iRet++;
			ArrayList<xTree<typeOb>> alStackBuf = new ArrayList<xTree<typeOb>>();
			for (xTree<typeOb> n : alStack) {
				for (xTree<typeOb> nCh : n.getChildren()) {
					alStackBuf.add(nCh);
				}
			}
			alStack = alStackBuf;
		}
		return(iRet);
	}
	/**
	 * Получить самый рутовый элемент дерева
	 */
	public xTree<typeOb> getRootest()
	{
		xTree<typeOb> node = this;
		while(node.getParent() != null)
		{
			node = (xTree<typeOb>) node.getParent();
		}
		return node;
	}

	/**
	 * Получить текущий уровень в дереве
	 * @return
	 */
	public int getCurentLavel()
	{
		xTree<typeOb> node = this;
		int iRet = 1;
		while(node.getParent() != null)
		{
			node = (xTree<typeOb>) node.getParent();
			iRet++;
		}
		return iRet;
	}
	/**
	 * Получить кол-во всех узлов начиная с этого, самого себя учитываем
	 * @return
	 */
	public int getCountAllChildren(int iCurCount)
	{
		iCurCount++;
		for(xTree<typeOb> node: children)
		{
			iCurCount = node.getCountAllChildren(iCurCount);
		}
		return iCurCount;
	}

	public Vector<xTree> getChildren()
	{
		return children;
	}

	public void setParent(xTree<typeOb> in_parent)
	{
		this.parent = in_parent;
	}

	public xTree<typeOb> addNode(xTree<typeOb> nodeTreeVlan)
	{
		nodeTreeVlan.setParent(this);
		children.add(nodeTreeVlan);
		return this;
	}

	public boolean isImloaded()
	{
		boolean bRet = false;
		synchronized(imloaded)
		{
			bRet = imloaded;
		}
		return bRet;
	}

	public void setImloaded()
	{
		synchronized(imloaded)
		{
			imloaded = true;
		}
	}

	/**
	 * Установка, что я загружен, требуется для загрузки налету
	 */
	public void setImNoloaded()
	{
		synchronized(imloaded)
		{
			imloaded = false;
		}
	}

	@Override
	public int compareTo(xTree<typeOb> o)
	{
		return 0;
	}

	public xTree(typeOb in_ob)
	{
		ob = in_ob;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		ArrayList<xTree<typeOb>> listEl = getAllNodeInList();
		for(xTree<typeOb> el: listEl)
		{
			sb.append(el.getOb().toString()).append(delimToString);
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	/**
	 * Получить строку из текущего узла
	 * @return
	 */
	public String toOut()
	{
		return ob.toString();
	}

	@Override
	public TreeNode getChildAt(int childIndex)
	{
		fillChildren();
		TreeNode nRet = (TreeNode) children.get(childIndex);
		return nRet;  //To change body of implemented methods use File | Settings | File Templates.
	}

	//----------------------------------------------------------
	@Override
	public int getChildCount()
	{
		fillChildren();
		int iRet = children.size();
		return iRet;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public TreeNode getParent()
	{
		TreeNode nRet = (TreeNode) parent;
		return nRet;
	}

	@Override
	public int getIndex(TreeNode node)
	{
		fillChildren();
		int iRet = children.indexOf(node);
		return iRet;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean getAllowsChildren()
	{
		fillChildren();
		boolean bRet = (children.size() > 0);
		return bRet;
	}

	@Override
	public boolean isLeaf()
	{
		fillChildren();
		boolean bRet = (children.size() == 0);
		return bRet;
	}

	@Override
	public Enumeration children()
	{
		fillChildren();	// На случай заполнения по факту обращения
		Enumeration en = children.elements();
		return en;  //To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Функция для преропределения в потомке, если надо заполнить детей на ходу
	 */
	public void fillChildren()
	{
	}

	/**
	 * Удаляет узел из текущих детей, если нет в детях, то не удаляет
	 * @param nodeTreeVlan
	 * @return
	 */
	public boolean removeNode(xTree<typeOb> nodeTreeVlan)
	{
		boolean bRet = false;
		// Найдем есть ли у нас этот узел
		if(getChildren().remove(nodeTreeVlan))
		{
			nodeTreeVlan.setParent(null);
			bRet = true;
		}
		return(bRet);
	}

	/**
	 * Получить все узлы от текущего до первопредка
	 * @return
	 */
	public ArrayList<Object> getPath()
	{
		ArrayList<Object> alRet = new ArrayList<Object>();

		getPath(alRet);
		return alRet;
	}
	private void getPath(ArrayList<Object> alRet)
	{
		alRet.add(0, this);
		if(getParent() != null)
		{
			((xTree)getParent()).getPath(alRet);
		}
	}


}
