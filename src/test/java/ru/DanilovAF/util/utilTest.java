package ru.DanilovAF.util;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;

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
}
























