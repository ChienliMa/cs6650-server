package edu.neu.cs6650;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.SplittableRandom;


public class Profiler {
	private static Profiler instance = null;
	
	private  SplittableRandom randomGenerator = new SplittableRandom(222);
	private  final int nodeCount = 100;
	private  ConcurrentHashMap<Integer, List<String>> logger = new ConcurrentHashMap<Integer, List<String>>();;
	
	private  Date currDate;	
	
	public static Profiler getInstance() {
		if (instance == null) {
			instance = new Profiler();
		}
		return instance;
	}
	
	public void log(Date time, String log) {
		if (!time.equals(currDate)) { dump(); }
		
		int key = randomGenerator.nextInt(nodeCount);
		List<String> queue = logger.get(key);
		if (queue == null) {
			queue = new ArrayList<String>();
			logger.put(key, queue);
		}
		queue.add(log);
	}

	public synchronized void dump() {
		final ConcurrentHashMap<Integer, List<String>> logs = logger;
		final Date date = currDate;
		new Thread() {
			public void run() {			
				dumpLogIntoDB(logs, date); 
			}
		}.run();
		
		logger = new ConcurrentHashMap<Integer, List<String>>();
		currDate = new Date();
		return;
	}
	
	public void dumpLogIntoDB(ConcurrentHashMap<Integer, List<String>>  logs, Date date) {
	    Connection connection = null;
	    PreparedStatement statement = null;
	    try {
	        connection = (Connection) ConnectionPool.getConnection();
	        statement = connection.prepareStatement("insert into Logs values (?,?,?,?)");
	        Timestamp timestamp = new Timestamp(date.getTime());
			for (Integer threadId: logs.keySet()) {
				for(String log: logs.get(threadId)) {
					String[] cols = log.split(",");
					statement.setString(1, cols[0]);
					statement.setTimestamp(2,timestamp);
					
					if (cols[0] == "SUCC") {
						statement.setInt(3, Integer.parseInt(cols[1]));
						statement.setInt(4, Integer.parseInt(cols[2]));
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

