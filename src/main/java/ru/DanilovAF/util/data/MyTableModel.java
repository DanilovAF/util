package ru.DanilovAF.util.data;

import ru.DanilovAF.util.Json.JsonN;
import ru.DanilovAF.util.MyException;
import ru.DanilovAF.util.util;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchResult;
import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.DanilovAF.util.Json.JsonN.TYPE_DIM;
import static ru.DanilovAF.util.Json.JsonN.TYPE_OBJECT;
import static ru.DanilovAF.util.data.ItemData.VM;

/**
 * Класс для работы с селектами из БД
 * получаем RecordSet и вливаем его в таблицу в памяти, которую можно сразу запихать в таблицу Swing-а
 */
public class MyTableModel extends AbstractTableModel {

    private static final Logger log = LoggerFactory.getLogger(MyTableModel.class);

    private String tableName = "";
    protected ArrayList<ItemData> data = new ArrayList<ItemData>();   // Сами данные
    protected DictData dict = new DictData();
    protected ArrayList<String> alColumnNames = new ArrayList<String>();
    ArrayList<Integer> alHiddenColumns = null;        // Скрытык колонки
    HashSet<Integer> hsHiddenColumns = new HashSet<Integer>();        // Скрытык колонки в индексе
    private int[] dimSubst;

    public MyTableModel() {
    }

    public StringBuffer toOutLine(StringBuffer sb) {
        if (sb == null)
            sb = new StringBuffer();
        // Пройдем по всем столбикам - получим размер строк
        // Сравним с длинной заголовка поля, если оно длиннее, то исправим
        int iLenFieldName = 0;
        for(String sName: dict.getDict().keySet())
        {
            if(sName.length() > iLenFieldName)
                iLenFieldName = sName.length();
        }

        // Добавми данные, проходим по максимальному кол-ву значений  (сначала получим максимальное кол-во значений в этой записи)
        for (ItemData item : data) {
            int iRow = 0;
            for (String sVal : item.item) {
                // Выводим имя атрибута
                sb.append("\n").append(util.alignString(dict.getColumnName(iRow), " ", iLenFieldName));
                int iVm = 1;
                for(String sVm: sVal.split(VM, -1)) {
                    if(iVm == 1) {
                        sb.append(" : ").append(sVm);
                    } else {
                        sb.append("\n").append(util.alignString("", " ", iLenFieldName)).append("   ").append(sVm);
                    }
                    iVm++;
                }
                iRow++;
            }
            sb.append("\n-------------------");
        }
        return sb;

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
        for (ItemData item : data) {
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
            if(sName == null)
                sName = "";
            if(!hsHiddenColumns.contains(y)) {
                sb.append(" ");
                if (sName.length() > alLen.get(y)) {
                    sb.append(sName.substring(0, alLen.get(y)));
                } else {
                    sb.append(util.alignStringLeft(sName, " ", alLen.get(y)));
                }
            }
            y++;
        }
        // Добавми разделитель заголовка
        sb.append("\n");
        y = 0;
        for (Integer iVal : alLen) {
            String sName = dict.getColumnName(y);
            if(!hsHiddenColumns.contains(y)) {
                sb.append(" ").append(util.alignStringLeft("-", "-", alLen.get(y)));
            }
            y++;
        }
        // Добавми данные, проходим по максимальному кол-ву значений  (сначала получим максимальное кол-во значений в этой записи)
        for (ItemData item : data) {

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
                        if(!hsHiddenColumns.contains(y)) {
                            sVal = util.field(sVal, ItemData.VM, i);
                            if (sVal != null) {
                                sb.append(" ").append(util.alignStringLeft(sVal, " ", alLen.get(y)));
                            } else {
                                sb.append(" ").append(util.alignStringLeft(" ", " ", alLen.get(y)));
                            }
                        }
                        y++;
                    }
//                if(i > 1)
                    sb.append("\n");
                }
                sb.deleteCharAt(sb.length() - 1);
            }
            int u = 0;
        return sb;
    }

    public MyTableModel(DictData dict) {
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
            if(node.get(sPath).getValType() == TYPE_DIM) {
                for (JsonN n : node.get(sPath).getDim()) {
//                    ItemData item = new ItemData(getDict());
                    ItemData item = new ItemData(getDict());
                    HashMap<String, JsonN> hmVals = n.getMap();
                    for (String sKey : hmVals.keySet()) {
                        sKey = convertKeyIN(sKey);
                        String sVal = hmVals.get(sKey).getVal();
                        sVal = convertValIN(sVal);
                        item.addAlways(sKey, sVal);
                    }
                    addItem(item);
                }
            } else if(node.get(sPath).getValType() == TYPE_OBJECT)
            {   // Это объект
                JsonN n = node.get(sPath);
                ItemData item = new ItemData(getDict());
                HashMap<String, JsonN> hmVals = n.getMap();
                for (String sKey : hmVals.keySet()) {
                    sKey = convertKeyIN(sKey);
                    String sVal = hmVals.get(sKey).getVal();
                    sVal = convertValIN(sVal);
                    item.addAlways(sKey, sVal);
                }
                addItem(item);
            }
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public MyTableModel(JsonN node) throws MyException {
        addNodeToTable(node, null, null, 0);
    }

    public MyTableModel addNodeToTable(JsonN node, String sKeyParent, ItemData item, int lavael) throws MyException {
        if(item == null) {
            item = new ItemData(getDict());
        }
        if(node != null && !node.isEmpty())
        {
            int iType = node.getValType();
            if(iType == TYPE_DIM) {
                for (JsonN n : node.getDim()) {
                    iType = n.getValType();
                    if(iType == TYPE_DIM || iType == TYPE_OBJECT) {
                        // Рекурсия
                        addNodeToTable(n, sKeyParent, item, lavael + 1);
                    } else {
                        // Здесь идут значения массива, их надо добавлять в таблицу как значения
                        String sVal = n.getVal();
                        sVal = convertValIN(sVal);
                        if(item.getDict().containsKey(sKeyParent)) {
                            item.addVm(sKeyParent, sVal);
                        } else {
                            item.addAlways(sKeyParent, sVal);
                        }
                        int y = 0;
                    }
                    if(lavael == 0) {
                        addItem(item);
                        item = new ItemData(getDict());
                    }
                }
            } else if(iType == TYPE_OBJECT) {
                HashMap<String, JsonN> hmVals = node.getMap();
                for (String sKey : hmVals.keySet()) {
                    sKey = convertKeyIN(sKey);
                    iType = hmVals.get(sKey).getValType();
                    if(iType == TYPE_DIM || iType == TYPE_OBJECT) {
                        // Рекурсия
                        addNodeToTable(hmVals.get(sKey), sKey, item, lavael + 1);
                    } else {
                        String sVal = hmVals.get(sKey).getVal();
                        sVal = convertValIN(sVal);
                        item.addAlways(sKey, sVal);
                    }
                    if(lavael == 0) {
                        addItem(item);
                        item = new ItemData(getDict());
                    }
                }
            }
        }
        return null;
    }

    /**
     * Метод запускается перед вставкой данных в запись в конструкторе от JsonN
     * @param sVal
     * @return
     */
    protected String convertValIN(String sVal) {
        return sVal;
    }

    /**
     * Метод для возможности конвертирования строки перед вставкой в таблицу в конструкторе от JsonN
     * @param sKey
     * @return
     */
    protected String convertKeyIN(String sKey) {
        return sKey;
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
                ItemData item = new ItemData(dict);
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
    public static MyTableModel executeSql(Connection connection, String sql) throws SQLException {
        MyTableModel mtm = null;
        Statement st = null;
        ResultSet rSt = null;
        if(log.isTraceEnabled()) { log.trace("SelectTo => " +sql); }
        st = connection.createStatement();
        rSt = st.executeQuery(sql);
        mtm = new MyTableModel(rSt);
        st.close();
        rSt.close();
        return mtm;
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
                ItemData item = new ItemData(dict);
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
                ItemData item = new ItemData(dict);
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
                for (ItemData it : data) {
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
//					for(ItemData it : data)
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

    public void hideCollumn(String sColName)
    {
        if(getDict().isContainField(sColName)) {
            int ind = getDict().getColumnNum(sColName);
            if(alHiddenColumns == null)
                alHiddenColumns = new ArrayList<Integer>();
            alHiddenColumns.add(ind);
            hsHiddenColumns.add(ind);
        }
    }

    public ItemData getNewItem() {
        return new ItemData(dict);
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
        Collections.sort(data, new Comparator<ItemData>() {
            public int compare(ItemData o1, ItemData o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public DictData giveMeDict() {
        DictData dRet = dict;
        dict = null;
        return dRet;
    }

    public ArrayList<ItemData> giveMeData() {
        ArrayList<ItemData> aRet = data;
        data = null;
        return aRet;
    }

    /**
     * Просто добавляет пустую строку это нужно для пустых таблиц
     */
    public void addEmptyItem() {
        ItemData item = new ItemData(dict);
        for (String sName : alColumnNames) {
            item.set(sName, "");
        }
        data.add(item);
    }

    public void addItem(ItemData in_it) {
        ItemData item = new ItemData(in_it);    // Копирование записи
        item.setDict(dict);    // Просто заменим словарь - все на откуп программера
        data.add(item);        // добавили запись
    }

    public ItemData getItem(int iPos) {
        return data.get(iPos);
    }

    public ArrayList<ItemData> getData() {
        return data;
    }

    public DictData getDict() {
        return dict;
    }
    public boolean isEmpty()
    {
        boolean bRet = false;
        if(data == null)
            bRet = true;
        if(data.isEmpty())
            bRet = true;
        return bRet;
    }
}


















