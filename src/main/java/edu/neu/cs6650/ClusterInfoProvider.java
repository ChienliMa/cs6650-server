package edu.neu.cs6650;

public class ClusterInfoProvider {
	private static String MasterIp = "";
	
	public static void setMasterIp(String masterIp) {
		MasterIp = masterIp;
	}
	public static String getMasterIp() {
		return MasterIp;
	}
}
