package edu.neu.cs6650;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LogDao {
	public static void inserytLogs(ConcurrentHashMap<Integer, List<Log>> logs) {
		Connection connection = null;
	    PreparedStatement statement = null;
	    try {
	        connection = (Connection) ConnectionPool.getConnection();
	        statement = connection.prepareStatement("insert into Logs values (?,?,?,?)");
			for (Integer threadId: logs.keySet()) {
				for(Log log: logs.get(threadId)) {
					statement.setString(1, log.getStatus());
					statement.setTimestamp(2, new Timestamp(log.getTimeMillis()));
					
					if (log.getStatus() == "SUCC") {
						statement.setLong(3, log.getDbTime());
						statement.setLong(4, log.getTotalTime());
					} else {
						statement.setInt(3, 0);
						statement.setInt(4, 0);
					}
			        	statement.addBatch();
				}
			}
	        statement.executeBatch();
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    finally {
			try {
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
