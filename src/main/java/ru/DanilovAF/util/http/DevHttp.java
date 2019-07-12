package ru.DanilovAF.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aleksandr.danilov on 16.01.2019.
 *
 * Класс для запросов к устройству HTTP
 *
 *
 */
public class DevHttp {

    private static final Logger log = LoggerFactory.getLogger(DevHttp.class);

    public StringBuffer doCmdHttp(String in_sCmd) throws IOException
    {
        String sUrl = "";
        sUrl = in_sCmd;
        if(log.isTraceEnabled()) { log.trace("Запрос :" + sUrl); }
        URL url = new URL(sUrl);

        BufferedReader inWeb = null;
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        inWeb = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;

        StringBuffer sb = new StringBuffer();
        while ((line = inWeb.readLine()) != null)
        {
            sb.append(line);
            sb.append("\n");
        }
        inWeb.close();
        if(log.isTraceEnabled()) { log.trace("Результат исполнения :\n" + sb.toString()); }
        return(sb);
    }

}
