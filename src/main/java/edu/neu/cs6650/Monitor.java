package edu.neu.cs6650;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class Monitor {
	private static Monitor instance = null;
	private  ConcurrentHashMap<Long, List<Log>> logger = new ConcurrentHashMap<Long, List<Log>>();;
	private  Date currDate = new Date();	
	
	public static Monitor getInstance() {
		if (instance == null) {
			instance = new Monitor();
		}
		return instance;
	}
	
	public void log(Log log, Date date) {
		if (date.after(currDate)) { dump(); }
		
		ConcurrentHashMap<Long, List<Log>> logs = logger;
		
		Long key = Thread.currentThread().getId();
		List<Log> queue = logs.get(key);
		if (queue == null) {
			queue = new ArrayList<Log>();
			logs.put(key, queue);
		}
		queue.add(log);
	}

	public void dump() {
		final ConcurrentHashMap<Long, List<Log>> oldLogger = logger;
		logger = new ConcurrentHashMap<Long, List<Log>>();
		currDate = new Date(System.currentTimeMillis()+3000l);

		ArrayList<Log> logs = new ArrayList<Log>();
		for (Long threadId: oldLogger.keySet()) {
			List<Log> threadLogs = oldLogger.get(threadId);
			int size = threadLogs.size();
			for(int i=0;i<size;i += 1) logs.add(threadLogs.get(i));
		}
		
//		LogDao.inserytLogs(logs); 
		return;
	}
}

