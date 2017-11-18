package edu.neu.cs6650;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import javax.ws.rs.QueryParam;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.naming.InitialContext;
import javax.naming.NamingException;


@Path("resort")
public class Hw2Server {
	private BasicDataSource getDataSource() throws NamingException {
		InitialContext ctx = new InitialContext();
		return (BasicDataSource)ctx.lookup("java:comp/env/jdbc/cs6650");
	}
	
	private Response ErrorHandler(Exception e) { 
		Profiler.getInstance().log(new Log("ERROR", System.currentTimeMillis(), 0, 0));
		return Response.status(500).entity(e.getMessage()).build();
	}
	
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String simpleGet() {
		return "ddd";
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
		String query = String.format("select sum(CNT * height) as TotalHeight" +
				"	 from ((select LiftRides.LiftID as id, count(*) as CNT" +
				"    from LiftRides" +
				"	where LiftRides.SkierID = %d and LiftRides.Day = %d" +
				"	group by LiftRides.LiftID) as t" +
				"	left join Lifts on t.id = Lifts.id);", skierID, dayNum);
		
		BasicDataSource ds = null;
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Integer rval = -1;
		try {
			ds = this.getDataSource();
			
			dbStart = System.currentTimeMillis();
			con = ds.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			dbEnd = System.currentTimeMillis();
			
			if (rs.next()) {
				rval = rs.getInt("TotalHeight");
			}
		} catch (SQLException e) {
			return this.ErrorHandler(e);
		} catch (NamingException e) {
			return this.ErrorHandler(e);
		}finally{
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
			serviceEnd = System.currentTimeMillis();
		}
		
		Profiler.getInstance()
					.log(new Log("SUCC", System.currentTimeMillis(),
									new Integer((int) (serviceEnd - serviceStart)), new Integer((int) (dbEnd - dbStart))));
		// Generated loGenerated MessageBodyFactory () :
		return Response.status(200).entity(rval).build();
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
		String query = String.format("insert into LiftRides (ResortID, Day, SkierID, LiftID, Time) " +
				" value(%d, %d, %d, %d, %d);", resortID, dayNum, skierID, liftID, timestamp);

		Connection con = null;
		Statement stmt = null;
		try {
			BasicDataSource ds = this.getDataSource();
			dbStart = System.currentTimeMillis();
			
			con = ds.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			
			dbEnd = System.currentTimeMillis();

		} catch (SQLException e) {
			return this.ErrorHandler(e);
		} catch (NamingException e) {
			return this.ErrorHandler(e);
		}finally{
			try {
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			serviceEnd = System.currentTimeMillis();
		}
		
		Profiler.getInstance()
				.log(new Log("SUCC", System.currentTimeMillis(),
						new Integer((int) (serviceEnd - serviceStart)), new Integer((int) (dbEnd - dbStart))));
		return Response.status(200).build();
    }         
}
