package ru.DanilovAF.util.http;

import ru.DanilovAF.util.Json.JsonN;
import ru.DanilovAF.util.ThreadMSG.MsgStack;

/**
 * Created by aleksandr.danilov on 08.04.2019.
 */
public class HttpListner extends MsgStack<StringBuffer>
{
	StringBuffer sb = new StringBuffer();	// Весь вывод, который получим от зпроса

	/**
	 * Сюда прилетит все события из стека
	 * @param eRet
	 */
	@Override
	public void on_allMessage(StringBuffer eRet)
	{
		sb.append(eRet);
	}

	@Override
	public void on_lastMessage(StringBuffer eRet)
	{
		sb.append(eRet);
		// Проверим полученный вывод на ошибки
//		log.debug("Получили ответ от сервера\n" + sb);
	}
	@Override
	public String toString()
	{
		return sb.toString();
	}
	public StringBuffer getAnswer()
	{
		StringBuffer nRet = new StringBuffer();
		if(sb != null && sb.length() > 0) {
			int iPosB = sb.indexOf("\r\n\r\n");
			if(iPosB != -1) {
				nRet.append(sb.substring(iPosB + "\r\n\r\n".length()));
			}
		}
		return(nRet);
	}
}

