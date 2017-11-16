package edu.neu.cs6650;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


@Path("/LogFile")
public class LogFile {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)           
	public String getFile(@QueryParam("logTime") Long logTime) {
		return "LOL";
	}
}
