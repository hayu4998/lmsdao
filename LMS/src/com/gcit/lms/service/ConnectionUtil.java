package com.gcit.lms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {
	public String driverName = "com.mysql.jdbc.Driver";
	public String url = "jdbc:mysql://localhost/library?useSSL=true";
	public String username = "root";
	public String password = "Yh19930718";
	
	public Connection getConnection() throws SQLException, ClassNotFoundException{
		Class.forName(driverName);
		Connection conn = DriverManager.getConnection(url, username, password);
		conn.setAutoCommit(Boolean.FALSE);
		
		return conn;
	}
}
