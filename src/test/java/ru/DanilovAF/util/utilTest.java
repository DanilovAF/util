package ru.DanilovAF.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by Aleksandr.Danilov on 03.12.2018.
 */
public class utilTest {
    @Test
    public void test_isHexString_metod() throws Exception {
        String sIn = "42:6c:61:63:6b:20:43:61:72:74:72:69:64:67:65:20:48:50:20:43:43:33:36:34:58:00";
        boolean bRet = util.isHexString(sIn);
        assertThat(bRet).isTrue();
    }

    @Test
    public void test_isHexString_metod2() throws Exception {
        String sIn = "CNFY109984";
        boolean bRet = util.isHexString(sIn);
        assertThat(bRet).isFalse();
    }
    @Test
    public void test_isHexString_metod3() throws Exception {
        String sIn = "d0:a7:d0:b5:d1:80:d0:bd:d1:8b:d0:b9:20:d1:82:d0:be:d0:bd:d0:b5:d1:80:2d:d0:ba:d0:b0:d1:80:d1:82:d1:80:d0:b8:d0:b4:d0:b6:2c:20:50:4e:20:47:65:6e:75:69:6e:65:20:58:65:72:6f:78:28:52:29:20:54:6f:6e:65:72:3b:53:4e:38:30:42:30:36:30:30:35:32:41:30:37:30:30:30:30";
        boolean bRet = util.isHexString(sIn);
        assertThat(bRet).isTrue();
    }

    @Test
    public void test_isHexString_metod_not() throws Exception {
        String sIn = "42:6c:61:63:6b:20:G3:61:72:74:72:69:64:67:65:20:48:50:20:43:43:33:36:34:58:00";
        boolean bRet = util.isHexString(sIn);
        assertThat(bRet).isFalse();
    }

    @Test
    public void test_hexStringTostring_metod() throws Exception {
        String sIn = "42:6c:61:63:6b:20:43:61:72:74:72:69:64:67:65:20:48:50:20:43:43:33:36:34:58:00";
        String sRet = util.hexStringTostring(util.replace(sIn, ":", ""));
        assertThat(sRet).isEqualTo("Black Cartridge HP CC364X\u0000");
    }

    @Test
    public void test_hexStringTostring_metod2() throws Exception {
        String sIn = "d0:a7:d0:b5:d1:80:d0:bd:d1:8b:d0:b9:20:d1:82:d0:be:d0:bd:d0:b5:d1:80:2d:d0:ba:d0:b0:d1:80:d1:82:d1:80:d0:b8:d0:b4:d0:b6:2c:20:50:4e:20:47:65:6e:75:69:6e:65:20:58:65:72:6f:78:28:52:29:20:54:6f:6e:65:72:3b:53:4e:38:30:42:30:36:30:30:35:32:41:30:37:30:30:30:30";
        String sRet = util.hexStringTostring(util.replace(sIn, ":", ""));
        assertThat(sRet).isEqualTo("Черный тонер-картридж, PN Genuine Xerox(R) Toner;SN80B060052A070000");
    }

    @Test
    public void test_getCurDir() throws Exception {
        String sFile = util.getCurDir();
        System.out.println(sFile);
    }

    @Test
    public void test_count() throws Exception {
        String sTest = "\n1\n2\n3\n";
        System.out.println(util.count(sTest, "\n"));

        sTest = "";
        System.out.println(util.count(sTest, "\n"));
    }

    @Test
    public void test_getQueryifOperStatus() throws Exception {

        ArrayList<String> sPorts = new ArrayList<String>();
        sPorts.add("818265-Alc7302-1-08-10");
        sPorts.add("8182347-Alc7302-2-05-29");
        sPorts.add("818222-Alc7302-3-01-33");
        sPorts.add("818241-Alc7302-2-03-40");
        sPorts.add("818241-Alc73-02-1-07-13");
        sPorts.add("818222-Alc7302-3-01-35");
        sPorts.add("8182459-Alc7302FD-11-03-25");
        sPorts.add("818220-Alc7302-1-07-24");
        sPorts.add("81822968-Alc7302-1-08-40");
        sPorts.add("81822561-Alc7302FD-11-13-10");
        sPorts.add("8182669-Alc7302-1-04-35");

        for(String sPort: sPorts) {
            System.out.println(util.getQueryifOperStatus(sPort));
        }
    }

    @Test
    public void test_tildaSpace1() throws Exception {
        String sParce = "п дшсп с тел \"\"";
        sParce = util.tildaSpace(sParce, ' ', '~');
        System.out.println(sParce);

        sParce = "п дшсп с тел \"1 2\"";
        sParce = util.tildaSpace(sParce, ' ', '~');
        System.out.println(sParce);

        sParce = "п дшсп с тел '\"1 2'";
        sParce = util.tildaSpace(sParce, ' ', '~');
        System.out.println(sParce);

        sParce = "п дшсп с тел '\"1 2";
        sParce = util.tildaSpace(sParce, ' ', '~');
        System.out.println(sParce);

        sParce = "п дшсп с name ' проба пера ' и с тел '\"1 2'";
        sParce = util.tildaSpace(sParce, ' ', '~');
        System.out.println(sParce);
    }

    @Test
    public void test_replaceVars() throws Exception {
        HashMap<String, String> hmVars = new HashMap<String, String>();
        hmVars.put("tel", "8182650368");
        hmVars.put("mac", "008190102030");
        String sStr = "tel";
        String sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "$tel$";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "$tel:4$";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "$tel:4,3$";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "tel=$tel:4,3$-$mac";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "tel=$tel:4,3$$mac";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "panas-$mac$.cfg";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "PHONE_NUMBER_1=\"Tel: $tel:2$\"";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

        sStr = "<Profile_Rule_B ua=\"na\">tftp://192.168.100.10/525/spa-525-$mac.xml</Profile_Rule_B>";
        sRet = util.replaceVars(sStr, null, hmVars);
        System.out.println(sStr + " = " + sRet);

    }

    @Test
    public void test_getFirsIp() throws Exception {
        long l = util.getIpNet("10.162.16.34", "255.255.254.0");
        System.out.println(l + " " + util.int2ip(l));

        l = util.getIpNet("10.162.17.34", "255.255.254.0");
        System.out.println(l + " " + util.int2ip(l));

        l = util.getIpNet("10.162.127.221", "255.255.254.252");
        System.out.println(l + " " + util.int2ip(l));

        l = util.getIpNet("10.162.46.2", "255.255.254.240");
        System.out.println(l + " " + util.int2ip(l));
    }
}
























