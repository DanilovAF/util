package ru.DanilovAF.util.data;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by aleksandr.danilov on 28.08.2019.
 */
public class DictDataTest {

	@Test
	public void test_DictData2() throws Exception {
		Connection conn = GetConnection.getConnectionOra("BILLING.af.atol.int", "BILLING", "dialer", "dialer");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM job");
		DictData dd = new DictData(rs);
		System.out.println(dd.getDict() + " -- " + dd.getTableName());
	}
}