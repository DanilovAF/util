package ru.DanilovAF.util;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: DanilovAF
 * Date: 11.12.2009
 * Time: 9:46:05
 * Логер для log4j.
 * Пишет в указанный каталог файлы по дням, в имени файла есть преффикс, постфикс и дата в формате от SimpleDateFormat
 */
public class myLog4jAppender extends AppenderBase<ILoggingEvent>
{
	FileWriter wr = null;
	String curDate;

    PatternLayoutEncoder encoder;
	private String separate = "/";  // По умолчанию для NIX систем

	public PatternLayoutEncoder getEncoder() {
      return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
      this.encoder = encoder;
    }


	/**
	 * Добавление лога в файл. При этом все исключения записи в файл подавляются
	 * @param event то что надо записть
	 */
//	protected void append(LoggingEvent event)
    protected void append(ILoggingEvent event)
	{
		try
		{
			if(wr == null)
			{
				// Определим систему, если это windows разделитель каталогов - свой
				OsCheck.OSType os = OsCheck.getOperatingSystemType();
				if(os.compareTo(OsCheck.OSType.Windows) == 0)
				{
					separate = "\\";
				}
				String fName = getFileName();
				wr = new FileWriter(getFileName(), true);
			}
			// Проверка на переход через сутки
			Date dateEnd = Calendar.getInstance().getTime();
			SimpleDateFormat sd = new SimpleDateFormat(simpleDateFormat);

			if(sd.format(dateEnd).compareTo(curDate) != 0)
			{
				curDate = sd.format(dateEnd);
				if(wr != null)
				{
					wr.close();
				}
				wr = new FileWriter(getFileName(), true);
			}
			//wr.write(getLayout().format(event));
            wr.write(this.encoder.getLayout().doLayout(event));
            //((AppenderBase<ILoggingEvent>)this).;
			wr.flush();
		} catch (IOException e)
		{
			//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	/**
	 * Получение имени файла из текущей даты, преффикса и суффикса
	 * @return строка - имя файла
	 */
	private String getFileName()
	{
//		System.out.println("getFileName !!!");
		Date dateEnd = Calendar.getInstance().getTime();
		SimpleDateFormat sd = new SimpleDateFormat(simpleDateFormat);
		curDate = sd.format(dateEnd);
		return(Directory + separate + Prefix + curDate + Suffix);
	}

	/**
	 * Закрытие всего открытого файла.
	 */
	public void close()
	{
		if(wr != null)
		{
			try
			{
				wr.close();
			} catch (IOException e)
			{
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	public boolean requiresLayout()
	{
		return true;
	}

	/**
	 * Директория в которой располагать файл лога.
	 */
	private String Directory;

	public String getDirectory()
	{
		return Directory;
	}

	public void setDirectory(String directory)
	{
//		System.out.println("setDirectory !!!");
		if(directory.charAt(0) == '%')
		{
			directory = " " + directory;
		}
		StringTokenizer st = new StringTokenizer(directory, "%");
		String sRet = "";
		int i = 1;
		while(st.hasMoreTokens())
		{
			String sBuf = st.nextToken();	// получили строку в виде wl_check,wl,2,2
			if(i%2 == 0)
			{	// Скорее всего это переменная из SET надо ее получить
				String sP = System.getenv().get(sBuf);
				if(sP != null && sP.compareTo("") != 0)
				{	// Есть такая переменная
					if(sRet.compareTo("") == 0)
					{
						sRet +=sP;
					} else
					{
						sRet += separate + sP;
					}
				}
			} else
			{
				if(sBuf.trim().compareTo("") != 0)
				{
					if(i == 1)
					{
						sRet += sBuf.trim();
					} else
					{
						sRet += separate + sBuf.trim();
					}
				}
			}
			i++;
		}

		File f = new File(sRet);
		if(f.isDirectory())
		{
			Directory = f.getAbsolutePath();
		} else
		{
			f.mkdirs();
			Directory = f.getAbsolutePath();
		}
//		System.out.println("setDirectory = " + Directory + "!!!!");
	}

	/**
	 * Преффикс в имени файла.
	 */
	private String Prefix;

	public String getPrefix()
	{
		return Prefix;
	}

	public void setPrefix(String prefix)
	{
		Prefix = prefix;
	}

	/**
	 * Суффикс в имени файла.
	 */
	private String Suffix;

	public String getSuffix()
	{
		return Suffix;
	}

	public void setSuffix(String suffix)
	{
		Suffix = suffix;
	}

	/**
	 * В каком формате писать дану в файле. Вскак в функции SimpleDateFormat
	 */
	private String simpleDateFormat;

	public String getSimpleDateFormat()
	{
		return simpleDateFormat;
	}

	public void setSimpleDateFormat(String simpleDateFormat)
	{
		this.simpleDateFormat = simpleDateFormat;
	}
}
