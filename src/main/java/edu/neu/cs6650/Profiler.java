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
import java.util.SplittableRandom;


public class Profiler {
	private static Profiler instance = null;
	
	private  SplittableRandom randomGenerator = new SplittableRandom(222);
	private  final int nodeCount = 100;
	private  ConcurrentHashMap<Integer, List<String>> logger = new ConcurrentHashMap<Integer, List<String>>();;
	
	private  Date currDate;	
	private  String monitorIP, monitorPort;

	
	public static Profiler getInstance() {
		if (instance == null) {
			instance = new Profiler();
		}
		return instance;
	}
	
	public  void setMonitorIP(String monitorIP) {
		monitorIP = monitorIP;
	}

	public  void setMonitorPort(String monitorPort) {
		monitorPort = monitorPort;
	}
	
	public  void log(Date time, String log) {
		if (!time.equals(currDate)) { dump(); }
		
		int key = randomGenerator.nextInt(nodeCount);
		List<String> queue = logger.get(key);
		if (queue == null) {
			queue = new ArrayList<String>();
			logger.put(key, queue);
		}
		queue.add(log);
	}

	private  String getURL(Long time) {
		return String.format("%s:%s/%s?time=%d", monitorIP,monitorPort,monitorURI, time);
	}
	
	public  synchronized void dump() {
		final ConcurrentHashMap<Integer, List<String>> oldLogger = logger;
		final Date oldDate = currDate;
		
		new Thread() {
			public void run() {			
				// Serialize logs and  
				StringBuilder stringBuilder  = new StringBuilder();
				for (Integer threadId: oldLogger.keySet()) {
					for(String log: oldLogger.get(threadId)) {
						stringBuilder.append(log);
					}
				}
				
				// post to monitor server
				HttpURLConnection httpCon = null;
				try {
					URL url = new URL(getURL(oldDate.getTime()));
					httpCon = (HttpURLConnection) url.openConnection();
					httpCon.setDoOutput(true);
					httpCon.setRequestMethod("PUT");
					OutputStreamWriter out = new OutputStreamWriter(
					    httpCon.getOutputStream());
					out.write(stringBuilder.toString());
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}.run();
		
		logger = new ConcurrentHashMap<Integer, List<String>>();
		currDate = new Date();
		return;
	}
}

