package ru.DanilovAF.util.data;

import ru.DanilovAF.util.Json.JsonN;
import ru.DanilovAF.util.MyException;
import ru.DanilovAF.util.util;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import javax.swing.table.AbstractTableModel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static ru.DanilovAF.util.data.ItemData2.VM;

/**
 * Класс для работы с селектами из БД
 * получаем RecordSet и вливаем его в таблицу в памяти, которую можно сразу запихать в таблицу Swing-а
 */
public class MyTableModel extends AbstractTableModel {
    private String tableName = "";
    protected ArrayList<ItemData2> data = new ArrayList<ItemData2>();   // Сами данные
    protected DictData2 dict = new DictData2();
    protected ArrayList<String> alColumnNames = new ArrayList<String>();
    ArrayList<Integer> alHiddenColumns = null;        // Скрытык колонки
    HashSet<Integer> hsHiddenColumns = new HashSet<Integer>();        // Скрытык колонки в индексе
    private int[] dimSubst;

    public MyTableModel() {
    }

    /**
     * Вывести в буфер таблицу в таблице по размеру данных
     *
     * @param sb
     * @return
     */
    public StringBuffer toOutTable(StringBuffer sb) {
        if (sb == null)
            sb = new StringBuffer();
        // Пройдем по всем столбикам - получим размер строк
        ArrayList<Integer> alLen = new ArrayList<Integer>();
        for (ItemData2 item : data) {
            for (int i = 0; i < item.size(); i++) {
                String sAm = item.get(i);
                if (alLen.size() < i + 1)
                    alLen.add(i, 0);
                if (sAm != null) {
                    // Пройдем по значениям
                    for (String sVm : sAm.split(VM)) {
                        if (sVm != null) {
                            if (alLen.get(i) < sVm.length())
                                alLen.set(i, sVm.length());
                        }
                    }
                }
                // Сравним с длинной заголовка поля, если оно длиннее, то исправим
                String sFieldName = dict.getDict().get("" + i);
                if (sFieldName != null && sFieldName.length() > alLen.get(i))
                    alLen.set(i, sFieldName.length());
            }
        }

        // выведем в sb таблицу
        // Сначала заголовок
        int y = 0;
        for (Integer iVal : alLen) {
            String sName = dict.getColumnName(y);
            sb.append(" ");
            if (sName.length() > alLen.get(y)) {
                sb.append(sName.substring(0, alLen.get(y)));
            } else {
                sb.append(util.alignStringLeft(sName, " ", alLen.get(y)));
            }
            y++;
        }
        // Добавми разделитель заголовка
        sb.append("\n");
        y = 0;
        for (Integer iVal : alLen) {
            sb.append(" ").append(util.alignStringLeft("-", "-", alLen.get(y)));
            y++;
        }
        // Добавми данные, проходим по максимальному кол-ву значений  (сначала получим максимальное кол-во значений в этой записи)
        for (ItemData2 item : data) {
            sb.append("\n");
            int iMaxVM = 0;        // Максимальное кол-во значений во всех атрибутах записи
            for (String sVal : item.item) {
                int iBuf = util.count(sVal, VM);
                if (iBuf > iMaxVM)
                    iMaxVM = iBuf;
            }
            iMaxVM++;    // Кол-во VM больше на 1-цу
            for (int i = 1; i <= iMaxVM; i++) {
                y = 0;
                for (String sVal : item.item) {
                    sVal = util.field(sVal, ItemData2.VM, i);
                    if (sVal != null) {
                        sb.append(" ").append(util.alignStringLeft(sVal, " ", alLen.get(y)));
                    } else {
                        sb.append(" ").append(util.alignStringLeft(" ", " ", alLen.get(y)));
                    }
                    y++;
                }
//                if(i > 1)
                    sb.append("\n");
            }
            sb.deleteCharAt(sb.length() - 1);
            int u = 0;
        }

        int g = 0;

        return sb;
    }

    public MyTableModel(DictData2 dict) {
        this.dict = dict;
    }

    /**
     * Получить таблицу из Json данных массива
     *
     * @param node
     * @param sPath
     */
    public MyTableModel(JsonN node, String sPath) {
        // Необходимо записхать вывод в MyTableModel
        try {
            for (JsonN n : node.get(sPath).getDim()) {
                ItemData2 item = new ItemData2(getDict());
                HashMap<String, JsonN> hmVals = n.getMap();
                for (String sKey : hmVals.keySet()) {
                    item.addAlways(sKey, hmVals.get(sKey).getVal());
                }
                addItem(item);
            }
        } catch (MyException e) {
            e.printStackTrace();
        }

    }

    /**
     * На базе рекорд сета построить таблицу
     *
     * @param ps
     */
    public MyTableModel(PreparedStatement ps) {
        try {
            ResultSetMetaData rsm = ps.getMetaData();
            int iCount = rsm.getColumnCount();
            for (int i = 1; i <= iCount; i++) {
                alColumnNames.add(rsm.getColumnName(i));
                dict.addField(rsm.getColumnName(i));
            }
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                int i = 1;
                String sBuf = "";
                ItemData2 item = new ItemData2(dict);
                for (String sCol : alColumnNames) {
                    sBuf = rs.getString(i);
                    item.set(i - 1, sBuf);
                    i++;
                }
                data.add(item);
            }
//			hideEmpyColumns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * На базе рекорд сета построить таблицу
     *
     * @param ps
     */
    public MyTableModel(ResultSet ps) {
        try {
            ResultSetMetaData rsm = ps.getMetaData();
            int iCount = rsm.getColumnCount();
            for (int i = 1; i <= iCount; i++) {
                alColumnNames.add(rsm.getColumnName(i));
                dict.addField(rsm.getColumnName(i));
            }
            ResultSet rs = ps;
            while (rs.next()) {
                int i = 1;
                String sBuf = "";
                ItemData2 item = new ItemData2(dict);
                for (String sCol : alColumnNames) {
                    sBuf = rs.getString(i);
                    item.set(i - 1, sBuf);
                    i++;
                }
                data.add(item);
            }
//			hideEmpyColumns();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Конструктор для отображения запроса в LDAP
     *
     * @param en      Результат запроса
     * @param sFields поля для вывода в таблицу
     */
    public MyTableModel(NamingEnumeration<SearchResult> en, String sFields, StringBuffer sbLog) {
        for (String sD : sFields.split(",")) {
            alColumnNames.add(sD);
            dict.addField(sD);
        }
        try {
            while (en.hasMore()) {
                SearchResult sr = en.nextElement();
                ItemData2 item = new ItemData2(dict);
                for (String sD : sFields.split(",")) {
                    StringBuffer sb = new StringBuffer();
//					BrowseLDAP.toOut(sr.getAttributes().get(sD), sb);
                    item.set(sD, util.field(sb.toString(), "-> ", 2));
                }
                data.add(item);
                // Запишем в лог, если есть буфер для тога
                if (sbLog != null) {
                    for (NamingEnumeration ae = sr.getAttributes().getAll(); ae.hasMoreElements(); ) {
                        Attribute at = (Attribute) ae.next();
//						BrowseLDAP.toOut(at, sbLog);
                    }
                }
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public void hideEmpyColumns() {
        // Если стоит флаг удаления пустых столбцов - удалим их
        if (true) {
            if (!data.isEmpty()) {
                for (ItemData2 it : data) {
                    if (alHiddenColumns == null)    // Первый раз заполняем все пустые поля в первой записи
                    {
                        alHiddenColumns = new ArrayList<Integer>();
                        for (int i = 0; i < it.getColumnCount(); i++) {
                            String sBuf = it.get(i);
                            if (sBuf != null) {
                                sBuf = util.trimLR(sBuf);
                                if (sBuf.isEmpty()) {
                                    alHiddenColumns.add(i);
                                }
                            } else {
                                alHiddenColumns.add(i);
                            }
                        }
                    } else {    // Второй раз идем только по ранее пустым колонкам
                        for (int z = alHiddenColumns.size() - 1; z >= 0; z--) {
                            String sBuf = it.get(alHiddenColumns.get(z));
                            if (sBuf != null && !util.trimLR(sBuf).isEmpty()) {
                                alHiddenColumns.remove(z);
                            }
                        }
                    }
                }
                if (!alHiddenColumns.isEmpty())    // Заполним индекс
                {
                    hsHiddenColumns.clear();
//					for(ItemData2 it : data)
                    {
                        for (Integer z : alHiddenColumns) {
                            hsHiddenColumns.add(z);
                        }
                    }
                }
                // Создадим массив подстановок индексов
                alHiddenColumns.clear();
                int iRealIndex = 0;
                for (int i = 0; i < alColumnNames.size(); i++) {
                    while (hsHiddenColumns.contains(iRealIndex)) {
                        iRealIndex++;
                    }
                    alHiddenColumns.add(iRealIndex);
                    iRealIndex++;
                }
                int y = 0;
            }
        }
    }

    public ItemData2 getNewItem() {
        return new ItemData2(dict);
    }

    // -----------------------------------------------------
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        int iRet = 0;
        iRet = alColumnNames.size();
        iRet -= hsHiddenColumns.size();
        return iRet;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(getRealColumnIndex(columnIndex));
    }

    @Override
    public String getColumnName(int column) {
        return alColumnNames.get(getRealColumnIndex(column));
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
//		return super.isCellEditable(rowIndex, columnIndex);
        return true;
    }
    // -----------------------------------------------------

    int getRealColumnIndex(int iIndex) {
        if (alHiddenColumns == null) {
            return iIndex;
        }
        return alHiddenColumns.get(iIndex);
    }

    public void setSortCol(int i) {
        dict.setSortCol(i);
        Collections.sort(data, new Comparator<ItemData2>() {
            public int compare(ItemData2 o1, ItemData2 o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public DictData2 giveMeDict() {
        DictData2 dRet = dict;
        dict = null;
        return dRet;
    }

    public ArrayList<ItemData2> giveMeData() {
        ArrayList<ItemData2> aRet = data;
        data = null;
        return aRet;
    }

    /**
     * Просто добавляет пустую строку это нужно для пустых таблиц
     */
    public void addEmptyItem() {
        ItemData2 item = new ItemData2(dict);
        for (String sName : alColumnNames) {
            item.set(sName, "");
        }
        data.add(item);
    }

    public void addItem(ItemData2 in_it) {
        ItemData2 item = new ItemData2(in_it);    // Копирование записи
        item.setDict(dict);    // Просто заменим словарь - все на откуп программера
        data.add(item);        // добавили запись
    }

    public ItemData2 getItem(int iPos) {
        return data.get(iPos);
    }

    public ArrayList<ItemData2> getData() {
        return data;
    }

    public DictData2 getDict() {
        return dict;
    }
}


















