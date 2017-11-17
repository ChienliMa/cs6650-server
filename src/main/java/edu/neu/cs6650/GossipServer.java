package edu.neu.cs6650;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/cluster")
public class GossipServer {
	private String nodesToString(List<String> nodes) {
		StringBuilder buffer = new StringBuilder();
		for(String node : nodes) {
			buffer.append(node);
			buffer.append(",");
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}
	
	@DELETE
	public String deleteNode(@QueryParam("ip") String ip) {
		ClusterInfoProvider provider = ClusterInfoProvider.getInsteance(); 
		provider.deleteNode(ip);
		return nodesToString(provider.getNodes());
	}
	
	@POST
	public String addNode(@QueryParam("ip") String ip) {
		ClusterInfoProvider provider = ClusterInfoProvider.getInsteance(); 
		provider.addNode(ip);
		return nodesToString(provider.getNodes());
	}
	
	@GET
	public String registerMonitor() {
		return nodesToString(ClusterInfoProvider.getInsteance().getNodes());
	}
}
