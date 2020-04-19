package ru.DanilovAF.util.Json;

import org.junit.Test;
import ru.DanilovAF.util.MyException;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static ru.DanilovAF.util.Json.JsonN.TYPE_DIM;

/**
 * Created by Aleksandr.Danilov on 27.12.2018.
 */
public class JsonNTest {

    @Test
    public void test_getKeyS() throws Exception {
        JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name").a(new JsonN().setiType(TYPE_DIM))).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
        String sKey = node.g("params").getVal("selectDiscoveries");
        assertThat(sKey).isEqualTo("extend");
        sKey = node.g("params,output").getDim().get(0).getVal("templateid");
        assertThat(sKey).isEqualTo("11");
    }

    @Test
    public void test_getKey() throws Exception {
        JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name").a(new JsonN().setiType(TYPE_DIM))).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
        String sKey = node.g("params,filter").getKey();
        assertThat(sKey).isEqualTo("host");
        try {
            sKey = node.getKey();
            fail();
        } catch (MyException e)
        {

        }
//        assertThat(sKey).isEqualTo("auth");
        try {
            sKey = node.g("params,output").getDim().get(0).getKey();
            fail();
        } catch (MyException e)
        {

        }
    }

    @Test
    public void test_getVal() throws Exception {
        JsonN node = new JsonN().p("jsonrpc", "2.0").p("params", new JsonN().p("selectDiscoveries", "extend").p("output", new JsonN().a(new JsonN().p("templateid", 11).p("nullDim", new JsonN().setiType(TYPE_DIM))).a("name").a(new JsonN().setiType(TYPE_DIM))).p("filter", new JsonN().p("host", new JsonN().a("Cisco CDP Neibors")))).p("id", 10).p("auth", (String) null);
        System.out.println(node.toString());
        String sKey = node.g("params,selectDiscoveries").getVal();
        assertThat(sKey).isEqualTo("extend");
        try {
            sKey = node.getVal();
            fail();
        } catch (MyException e)
        {

        }
    }

    @Test
    public void test_inputFileUTF_0A() {
        try {
            JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\test.json"));
            System.out.println(node.toStringJson(true));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_getNull() throws Exception {
        JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\test.json"));
        System.out.println(node.toStringJson(true));
        JsonN n = node.getNull("result,stop");
        assertThat(n).isNotNull();
        System.out.println(n.toStringJson(true));
        System.out.println(n.getVal());
        n = node.getN("result").getDim().get(0).getNull("mac");
        assertThat(n).isNotNull();
        System.out.println(n.toStringJson(true));
        n = node.getNull("result,stopp");
        assertThat(n).isNull();
        System.out.println(n);
        n = node.getNull("");
        assertThat(n).isNotNull();
        n = node.getNull(null);
        assertThat(n).isNotNull();
        n = node.getNull("result[2],mac");
        assertThat(n).isNotNull();
        System.out.println(n.toStringJson(true));
        n = node.getNull("result[-1],mac");
        assertThat(n).isNotNull();
        System.out.println(n.toStringJson(true));
    }

    /**
     * СОздать массив и заполнить его в цикле
     * Добавляется пустой объект, затем в него заталкиваем значения
     * @throws Exception
     */
    @Test
    public void test_addDim() throws Exception {
        JsonN n = new JsonN();
        n.a(new JsonN("groupid", new JsonN("10")));
        n.a(new JsonN("groupid", new JsonN("20")));
        n = new JsonN("groups", n);

        System.out.println(n.toStringJson(true));

        JsonN query = new JsonN();
        query.p("method", "host.update").p("params", new JsonN("hostid", "10").p(n));
        System.out.println(query.toStringJson(true));
        int y = 0;

    }

    @Test
    public void test_remove() throws Exception
    {
        JsonN node = JsonN.inputFileUTF_0A(new File("D:\\DALOV\\JAVA\\util\\testDel.json"));
        System.out.println(node.toStringJson(true));
        node.remove("result,interfaces");
        System.out.println(node.toStringJson(true));
    }
}













