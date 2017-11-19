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


public class Monitor {
	private static Monitor instance = null;
	
	private  SplittableRandom randomGenerator = new SplittableRandom(222);
	private  final int nodeCount = 100;
	private  ConcurrentHashMap<Integer, List<Log>> logger = new ConcurrentHashMap<Integer, List<Log>>();;
	
	private  Date currDate = new Date();	
	
	public static Monitor getInstance() {
		if (instance == null) {
			instance = new Monitor();
		}
		return instance;
	}
	
	public void log(Log log) {
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
		new Thread() {
			public void run() {			
				LogDao.inserytLogs(logs); 
			}
		}.run();
		
		logger = new ConcurrentHashMap<Integer, List<Log>>();
		currDate = new Date();
		return;
	}
}

