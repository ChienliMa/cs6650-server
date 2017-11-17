package edu.neu.cs6650;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;



public class LogHandler {
	private static LogHandler instance = null;
	private static Path cacheDir = Paths.get("/tmp/cs6650log");;
	
	public static LogHandler getInstance() {
		if(instance == null) {
			instance = new LogHandler();
		}
		return instance;
	}
	
	public void dumpLog(String node, Date time) {
		Path filePath = Paths.get(cacheDir.toString(), node, time.toString());
		

	}
	
	public void loadLog(String node, Date time) {
		File logFile = Paths.get(cacheDir.toString(), node, time.toString()).toFile();
	}
	
//	// get all the log we have
//	public String getStructures() {
//		
//	}
//	
//	
//	// given logs
//	// return logs that we dont have 
//	public List<String> difference() {
//		
//	}
	
}