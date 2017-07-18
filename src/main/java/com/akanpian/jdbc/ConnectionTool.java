package com.akanpian.jdbc;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionTool {
	public enum JDBCTYPE {

	}

	public static Connection getEmdbDerbyConnection() throws Exception {
		Class.forName("org.apache.derby.jdbc.JDBC");
		String p = System.getProperty("user.dir") + File.separator + "derbydb";
		return DriverManager.getConnection("jdbc:derby:" + p);
	}
}
