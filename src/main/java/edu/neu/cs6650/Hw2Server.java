package edu.neu.cs6650;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.ws.rs.QueryParam;

import java.security.Provider.Service;
import java.sql.SQLException;
import java.util.Date;
import javax.naming.NamingException;


@Path("resort")
public class Hw2Server {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String simpleGet() {
		return "ddd";
	}
	
	private Response errorHanlder(Exception e) {
		System.out.println("Service fail");
		System.out.println(e.getMessage());
		LogDao.getInstance().inserytLog(new Log("ERROR", 0l, 0l));
		return Response.status(500).entity(e.getMessage()).build();
	}
	
	@GET
	@Path("/myvert")
	@Produces(MediaType.TEXT_PLAIN)           
	public Response getStatus(
			@QueryParam("skierID") int skierID,
			@QueryParam("dayNum") int dayNum)
	{
		Long serviceStart, serviceEnd, dbStart, dbEnd;
		
		serviceStart = System.currentTimeMillis();
		Integer rval = -1;
		try {
			dbStart = System.currentTimeMillis();
			rval = LiftRidesDAO.getInstance().getVert(skierID, dayNum);
			dbEnd = System.currentTimeMillis();
		} catch (SQLException e) {
			return errorHanlder(e);
		} catch (NamingException e) {
			return errorHanlder(e);
		}

		serviceEnd = System.currentTimeMillis();
		LogDao.getInstance().inserytLog(new Log("SUCC", serviceEnd - serviceStart, dbEnd - dbStart));
		// Generated loGenerated MessageBodyFactory () :
		return Response.status(200).entity(rval.toString()).build();
	}
	
	@POST
	@Path("/load")
	@Produces(MediaType.TEXT_PLAIN)
	public Response postText(
			@QueryParam("resortID") int resortID,
			@QueryParam("dayNum") int dayNum,
			@QueryParam("timestamp") int timestamp,
			@QueryParam("skierID") int skierID,
			@QueryParam("liftID") int liftID)
	{
		Long serviceStart, serviceEnd, dbStart, dbEnd;
		serviceStart =System.currentTimeMillis();
		LiftRides liftRide = new LiftRides(resortID, skierID, liftID, dayNum, timestamp);
		
		try {
			dbStart = System.currentTimeMillis();
			LiftRidesDAO.getInstance().insertLiftRide(liftRide);
			dbEnd = System.currentTimeMillis();
		} catch (SQLException e) {
			return errorHanlder(e);
		} catch (NamingException e) {
			return errorHanlder(e);
		} 
		
		serviceEnd = System.currentTimeMillis();
		LogDao.getInstance().inserytLog(new Log("SUCC", serviceEnd - serviceStart, dbEnd - dbStart));
		return Response.status(200).build();
    }         
}
