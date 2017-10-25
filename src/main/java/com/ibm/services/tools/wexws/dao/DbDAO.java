package com.ibm.services.tools.wexws.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class DbDAO implements AccessLayerDAO {
	private String env = "systest";

	public DbDAO(String env) {
		this.env = env;
	}
	
	@Override
	public ResultSet executeQueryDB(String sql) throws Exception {
		String env = this.env;
		String DB_URL = "";
		System.out.println("ENV--->"+env+"<---");
		System.out.println("SQL-->"+ sql + "<--");
		Properties connectProperties = new Properties();
		
		// SYSTEST
		if (env.equalsIgnoreCase("systest")){
			DB_URL = "jdbc:db2://rcmpmpdb02.bld.dst.ibm.com:60004/BCSPMP";
			connectProperties.put("user", "pmpdev");
			connectProperties.put("password", "pmp4dev");
		} else if (env.equalsIgnoreCase("dev")){
			DB_URL = "jdbc:db2://rcmpmpdb01.bld.dst.ibm.com:60004/BCSPMP";
			connectProperties.put("user", "pmpdev");
			connectProperties.put("password", "pmp4dev");
		} else if (env.equalsIgnoreCase("systestMYSA")){
			DB_URL = "jdbc:db2://rcmpmpdb02.bld.dst.ibm.com:60004/BCSPMP";
			connectProperties.put("user", "mysatest");
			connectProperties.put("password", "new4mysa");
		} else if (env.equalsIgnoreCase("devMYSA")){
			DB_URL = "jdbc:db2://rcmpmpdb01.bld.dst.ibm.com:60004/BCSPMP";
			connectProperties.put("user", "mysadev");
			connectProperties.put("password", "dev@mysa");
		} else if (env.equalsIgnoreCase("systestTP")){
			DB_URL = "jdbc:db2://rcmpmptp02.boulder.ibm.com:60004/tlntpl";
			connectProperties.put("user", "pmpdev");
			connectProperties.put("password", "pmp4dev");
		} else { //devTP
			DB_URL = "jdbc:db2://rcmpmptp01.boulder.ibm.com:60004/tlntpl";
			connectProperties.put("user", "pmpdev");
			connectProperties.put("password", "pmp4dev");
		}
		
		Connection connection = null;
		 
		Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		connection = DriverManager.getConnection(DB_URL,connectProperties);
		 
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql); //"SELECT * FROM BCSPMP.PRCTNR_T FETCH FIRST 10 ROWS ONLY");
		
		//connection.close();
		
		return rs;
	}
}
