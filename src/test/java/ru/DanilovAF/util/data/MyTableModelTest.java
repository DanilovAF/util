package ru.DanilovAF.util.data;

import org.junit.Test;
import ru.DanilovAF.util.Json.JsonN;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 31.01.2019.
 */
public class MyTableModelTest {

    @Test
    public void test_toOutTable() throws Exception {
        DictData2 dict = new DictData2();
        dict.addField("id");
        dict.addField("name");

        MyTableModel tab = new MyTableModel(dict);
        ItemData2 item = new ItemData2(dict);
        item.set("id", "10");
        item.addVm("name", "Сергей");
        item.addVm("name", "Микола");
        tab.addItem(item);

        item = new ItemData2(dict);
        item.set("id", "20");
        item.set("name", "Павел");
        tab.addItem(item);

        item = new ItemData2(dict);
        item.set("id", "30");
        item.set("name", "Вова");
        tab.addItem(item);

        System.out.println(tab.toOutTable(null));
    }

    @Test
    public void test_MyTableModel_Json() throws Exception {
        JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\test.json"));

        MyTableModel tab = new MyTableModel(node, "result");
        System.out.println(tab.toOutTable(null));
    }

    @Test
    public void test_hideCollumn() throws Exception {
        JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\test.json"));

        MyTableModel tab = new MyTableModel(node, "result");
        tab.hideCollumn("start");
        System.out.println(tab.toOutTable(null));

    }

    @Test
    public void test_toOutLine() throws Exception {
        JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\test.json"));

        MyTableModel tab = new MyTableModel(node, "result");
        System.out.println(tab.toOutLine(null));
    }

}