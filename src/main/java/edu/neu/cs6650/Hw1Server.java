package edu.neu.cs6650;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hw1")
public class Hw1Server {
	@GET
	@Produces(MediaType.TEXT_PLAIN)           
	public String getStatus() {
	    return ("alive");
	}
	
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public int postText(String content) {
		 return (content.length());
}         

}
