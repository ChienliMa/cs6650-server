package edu.neu.cs6650;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterInfoProvider {
	private ConcurrentHashMap<String, Boolean> nodes = new ConcurrentHashMap<String, Boolean>();
	private static ClusterInfoProvider instance = null;
	
	public static ClusterInfoProvider getInsteance() {
		if (instance == null) {
			instance = new ClusterInfoProvider();
		}
		return instance;
	}
	
	public Boolean addNode(String ip) {
		Boolean rval = !nodes.containsKey(ip);
		nodes.put(ip, true);
		return rval;
	}
	
	public Boolean deleteNode(String ip) {
		Boolean rval = nodes.containsKey(ip);
		nodes.remove(ip);
		return rval;
	}
	
	public List<String> getNodes() {
		List<String> nodes = new ArrayList<String>();
		for (String node : this.nodes.keySet()) {
			nodes.add(node);
		}
		return nodes;
	}
}
