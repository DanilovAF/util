package ru.DanilovAF.util.data;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by aleksandr.danilov on 30.01.2019.
 */
public class ItemDataTest {

    @Test
    public void test_addVm() throws Exception {
        DictData dict = new DictData();
        dict.addField("id");
        dict.addField("name");

        ItemData item = new ItemData(dict);
        item.add("id", "10");
        item.add("name", "Иван");
        item.addVm("name", "Сергей");

        String ss = item.get("name");
        for(String s: ss.split(ItemData.VM))
        {
            System.out.println(s);
        }

        System.out.println(item);
    }

    @Test
    public void test_addVm2() throws Exception {
        DictData dict = new DictData();
        dict.addField("id");
        dict.addField("name");

        ItemData item = new ItemData(dict);
        item.add("id", "10");
        item.add("name", "Иван");
        item.addVm("name", "Сергей");
        item.addVm("name", "Микола");

        String ss = item.getVm("name", 1);
        System.out.println(ss);

        System.out.println(item);
    }

    @Test
    public void test_addAlways() throws Exception {
        DictData dict = new DictData();
        dict.addField("id");
        dict.addField("name");

        ItemData item = new ItemData(dict);
        item.add("id", "10");
        item.add("name", "Иван");
        item.addVm("name", "Сергей");
        item.addVm("name", "Микола");

        item.addAlways("age", "100");
        System.out.println(item);
    }

    @Test
    public void test_setCurItem() throws Exception {
        Connection conn = GetConnection.getConnectionOra("BILLING.af.atol.int", "BILLING", "dialer", "dialer");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM job");
        DictData dd = new DictData(rs);

        ItemData it = new ItemData(dd);
        System.out.println(it.getDict().toString());

        while (rs.next()) {
            it.setCurItem(rs);
            System.out.println(it);
        }


    }
}


















