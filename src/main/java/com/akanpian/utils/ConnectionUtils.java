package com.akanpian.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionUtils {
	public enum JDBCTYPE {

	}

	
//	Configuration conf = new Configuration();
	public static Connection getEmbeddedDerby() throws Exception {
		Class.forName("org.apache.derby.jdbc.JDBC");
		String p = System.getProperty("user.dir") + File.separator + "derbydb";
		return DriverManager.getConnection("jdbc:derby:" + p);
	}
}
