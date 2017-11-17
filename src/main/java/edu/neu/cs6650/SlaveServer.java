package edu.neu.cs6650;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/slave")
public class SlaveServer {
	@POST
	@Path("/registerMaster")
	public void registerMonitor(@QueryParam("masterIp") String masterIp) {
		ClusterInfoProvider.setMasterIp(masterIp);
	}
}
