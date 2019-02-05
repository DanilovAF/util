package ru.DanilovAF.util.data;

import org.junit.Test;
import sun.misc.VM;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 30.01.2019.
 */
public class ItemData2Test {

    @Test
    public void test_addVm() throws Exception {
        DictData2 dict = new DictData2();
        dict.addField("id");
        dict.addField("name");

        ItemData2 item = new ItemData2(dict);
        item.add("id", "10");
        item.add("name", "Иван");
        item.addVm("name", "Сергей");

        String ss = item.get("name");
        for(String s: ss.split(ItemData2.VM))
        {
            System.out.println(s);
        }

        System.out.println(item);
    }

    @Test
    public void test_addVm2() throws Exception {
        DictData2 dict = new DictData2();
        dict.addField("id");
        dict.addField("name");

        ItemData2 item = new ItemData2(dict);
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
        DictData2 dict = new DictData2();
        dict.addField("id");
        dict.addField("name");

        ItemData2 item = new ItemData2(dict);
        item.add("id", "10");
        item.add("name", "Иван");
        item.addVm("name", "Сергей");
        item.addVm("name", "Микола");

        item.addAlways("age", "100");
        System.out.println(item);
    }
}


















