package edu.neu.cs6650;

public class Log {
	private final String status;
	private final Long timeMillis;
	private final Integer dbTime;
	private final Integer totalTime;
	
	public Log(String status, Long timeMillis, Integer dbTime, Integer totalTime) {
		this.timeMillis = timeMillis;
		this.status = status;
		this.dbTime = dbTime;
		this.totalTime = totalTime;
	}
	
	public Long getTimeMillis() {
		return timeMillis;
	}
	
	public String getStatus() {
		return status;
	}

	public Integer getDbTime() {
		return dbTime;
	}

	public Integer getTotalTime() {
		return totalTime;
	}
}
