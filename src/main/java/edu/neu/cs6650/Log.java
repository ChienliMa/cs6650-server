package edu.neu.cs6650;

public class Log {
	private  String status = null;
	private  Long dbTime  = null;
	private  Long totalTime  = null;
	
	
	public Log(String status, Long dbTime, Long totalTime) {
		this.status = status;
		this.dbTime = dbTime;
		this.totalTime = totalTime;
	}
	

	
	public String getStatus() {
		return status;
	}

	public Long getDbTime() {
		return dbTime;
	}

	public Long getTotalTime() {
		return totalTime;
	}
}
