package edu.neu.cs6650;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LogDao {
	private static LogDao instance = null;
	private int batchSize = 10;
	private List<Log> logs;
	
	public LogDao() {
		logs = new ArrayList<Log>();
	}
	
	public static LogDao getInstance() {
		if (instance == null) {
			instance = new LogDao();
		}
		return instance;
	}
	
	public void inserytLog(Log log) {
		List<Log> batchLogs = null;
		synchronized (this) {
			if (logs.size() >= batchSize) {
				batchLogs = logs;
				logs = new ArrayList<Log>();
			}
			logs.add(log);
		}
		
		if (batchLogs == null) return;
		
		Connection connection = null;
	    PreparedStatement statement = null;
	    try {
	        connection = ConnectionPool.getConnection();
	        statement = connection.prepareStatement("insert into Logs values (?,?,?)");
			for(Log currLog: batchLogs) {
				statement.setString(1, currLog.getStatus());
				
				if (currLog.getStatus() == "SUCC") {
					statement.setLong(2, currLog.getDbTime());
					statement.setLong(3, currLog.getTotalTime());
				} else {
					statement.setInt(2, 0);
					statement.setInt(3, 0);
				}
		        	statement.addBatch();
			}
	        statement.executeBatch();
	    }
	    catch (Exception e) {
	    		System.out.println("Error dumpting Logs");
	        System.out.println(e.getMessage());
	    }
	    finally {
			try {
				if (statement != null) statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    }
	}
}
