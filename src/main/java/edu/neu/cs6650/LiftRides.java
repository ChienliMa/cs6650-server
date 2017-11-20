package edu.neu.cs6650;

public class LiftRides {
	public int getResortID() {
		return ResortID;
	}
	public void setResortID(int resortID) {
		ResortID = resortID;
	}
	public int getSkierID() {
		return SkierID;
	}
	public void setSkierID(int skierID) {
		SkierID = skierID;
	}
	public int getLiftID() {
		return LiftID;
	}
	public void setLiftID(int liftID) {
		LiftID = liftID;
	}
	public int getDay() {
		return Day;
	}
	public void setDay(int day) {
		Day = day;
	}
	public int getTime() {
		return Time;
	}
	public void setTime(int time) {
		Time = time;
	}
	public LiftRides(int resortID, int skierID, int liftID, int day, int time) {
		super();
		ResortID = resortID;
		SkierID = skierID;
		LiftID = liftID;
		Day = day;
		Time = time;
	}
	private int ResortID, SkierID, LiftID;
	private int Day, Time;
	 

}
