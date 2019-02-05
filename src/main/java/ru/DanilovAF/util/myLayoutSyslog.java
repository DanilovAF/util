package ru.DanilovAF.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
//import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 05.03.2010
 * Time: 9:54:24
 * Для вывода в сислог надо немного другой формат сообщений
 */
public class myLayoutSyslog extends LayoutBase<ILoggingEvent>

{
//	@Override
//	public String format(LoggingEvent event)
//	{
//		String sRet = event.getRenderedMessage();
//		sRet = util.field(sRet, "\n", 1);
//		int i = sRet.indexOf(":");
//		if(i != -1)
//		{
//			sRet = sRet.substring(i + 1).trim();
//		}
//		return sRet;
//	}


    @Override
    public String doLayout(ILoggingEvent event)
    {
        //String sRet = event.getRenderedMessage();
        String sRet = event.getFormattedMessage();
        sRet = util.field(sRet, "\n", 1);
        int i = sRet.indexOf(":");
        if(i != -1)
        {
            sRet = sRet.substring(i + 1).trim();
        }
        return sRet;
    }
}
