package ru.DanilovAF.util;

import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by Aleksandr.Danilov on 25.12.2018.
 */
public class OptTest {

    final static String OPT_PORT = "port:";	// Номер FXO порта
    final static String OPT_TEL = "tel:";	// Номер телефона
    final static String OPT_PASS = "pass:";	// пароль регистрации
    final static String OPT_DO = "do:";	// Что надо сделать, например присвоить set или почистить del
    final static String OPT_IP = "ip:";	// Что надо сделать, например присвоить set или почистить del


    @Test
    public void test_parseOpt() throws Exception {
        Opt opt = new Opt();
        opt.addParamName(OPT_IP);
        opt.addParamName(OPT_PORT);

        String[] args = new String[5];
        args[0] = "port:22";
        args[1] = "ip:192.168.1.1,192.168.1.2";
        args[2] = "Проба 1";
        args[3] = "Проба 2";

        opt.parseOpt(args);

        assertThat(opt.containsKey(OPT_IP)).isTrue();
        assertThat(opt.containsKey(OPT_PORT)).isTrue();
        assertThat(opt.getOneVal(OPT_PORT)).isEqualTo("22");
        assertThat(opt.getVals(OPT_IP)).isNotNull().hasSize(2);

        assertThat(opt).hasSize(2);
    }

    @Test
    public void test_parseOpt2() throws Exception {
        Opt opt = new Opt();
        opt.addParamName(OPT_PORT);
        opt.addParamName(OPT_TEL);
        opt.addParamName(OPT_PASS);
        opt.addParamName(OPT_DO);
        opt.addParamName(OPT_IP);

        String[] args = new String[5];
        args[0] = "do:del";
        args[1] = "port:22";
        args[2] = "ip:192.168.1.1";

        opt.parseOpt(args);

        assertThat(opt.containsKey(OPT_IP)).isTrue();
        assertThat(opt.containsKey(OPT_PORT)).isTrue();
        assertThat(opt.getOneVal(OPT_PORT)).isEqualTo("22");
        assertThat(opt.getVals(OPT_IP)).isNotNull().hasSize(1);
    }

}