package ru.DanilovAF.util.data;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by aleksandr.danilov on 22.08.2019.
 */
public class GetConnectionTest {

	@Test
	public void test_getConnectionOra() throws Exception {
		Connection conn = GetConnection.getConnectionOra("BILLING.af.atol.int", "BILLING", "dialer", "dialer");
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT * FROM job");
		MyTableModel tab = new MyTableModel(rs);
		System.out.println(tab.toOutTable(null));
	}
}