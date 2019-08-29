package ru.DanilovAF.util.data;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 28.08.2019.
 */
public class DictData2Test {

	@Test
	public void test_DictData2() throws Exception {
		Connection conn = GetConnection.getConnectionOra("BILLING.af.atol.int", "BILLING", "dialer", "dialer");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM job");
		DictData2 dd = new DictData2(rs);
		System.out.println(dd.getDict() + " -- " + dd.getTableName());
	}
}