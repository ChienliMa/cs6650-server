package edu.neu.cs6650;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class ConnectionPool {
	public static Connection getConnection() throws NamingException, SQLException {
		InitialContext ctx = new InitialContext();
		BasicDataSource dSource =  (BasicDataSource)ctx.lookup("java:comp/env/jdbc/ResortDb");
		return dSource.getConnection();
	}
	
}
