package edu.neu.cs6650;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.mysql.fabric.xmlrpc.base.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.SplittableRandom;


public class Profiler {
	private static Profiler instance = null;
	
	private  SplittableRandom randomGenerator = new SplittableRandom(222);
	private  final int nodeCount = 100;
	private  ConcurrentHashMap<Integer, List<Log>> logger = new ConcurrentHashMap<Integer, List<Log>>();;
	
	private  Date currDate = new Date();	
	
	public static Profiler getInstance() {
		if (instance == null) {
			instance = new Profiler();
		}
		return instance;
	}
	
	public void log( Log log) {
		Date time = new Date(log.getTimeMillis());
		if (!time.equals(currDate)) { dump(); }
		
		int key = randomGenerator.nextInt(nodeCount);
		List<Log> queue = logger.get(key);
		if (queue == null) {
			queue = new ArrayList<Log>();
			logger.put(key, queue);
		}
		queue.add(log);
	}

	public synchronized void dump() {
		final ConcurrentHashMap<Integer, List<Log>> logs = logger;
		final Date date = currDate;
		new Thread() {
			public void run() {			
				dumpLogIntoDB(logs, date); 
			}
		}.run();
		
		logger = new ConcurrentHashMap<Integer, List<Log>>();
		currDate = new Date();
		return;
	}
	
	public void dumpLogIntoDB(ConcurrentHashMap<Integer, List<Log>>  logs, Date date) {
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

