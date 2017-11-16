package edu.neu.cs6650;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/ClusterNodes")
public class ClusterNodes {
	@GET
	public String getNodes() {
		return "";
	}
	
	@PUT
	public void addNodes() {
		
	}
	
	@DELETE
	@Path("Node")
	public void deleteNode(@QueryParam("ip") String ip) {
		
	}
	
	@PUT
	@Path("Node")
	public void addNode(@QueryParam("ip") String ip) {
		
	}
	
	@GET
	@Path("Nodes")
	public void getNodes(@QueryParam("ip") String ip) {
		
	}
	
		
}
