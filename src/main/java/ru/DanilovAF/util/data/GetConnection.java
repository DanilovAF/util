package ru.DanilovAF.util.data;

import oracle.jdbc.driver.OracleDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.DanilovAF.util.http.HttpQuery;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by aleksandr.danilov on 22.08.2019.
 *
 * Получить соединение с ораклом для начала
 *
 */
public class GetConnection {

	private static final Logger log = LoggerFactory.getLogger(HttpQuery.class);

	public static Connection getConnectionOra(String server, String db, String username, String password) throws SQLException
	{
		Connection conn = null;
		if(server != null && !server.isEmpty() && db != null && !db.isEmpty() && username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
//			String thinConn = "jdbc:oracle:thin:@BILLING.af.atol.int:1521:BILLING";
			String thinConn = "jdbc:oracle:thin:@" + server + ":1521:" + db;
			Driver d = new OracleDriver();

			if (log.isTraceEnabled()) {
				log.trace("Connect :" + thinConn + " " + username);
			}
			conn = DriverManager.getConnection(thinConn, username, password);
			conn.setAutoCommit(true);
		}
		return conn;
	}

}
