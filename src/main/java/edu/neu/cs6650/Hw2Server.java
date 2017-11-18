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
			@QueryParam("dayNum") int dayNum) throws SQLException, NamingException 
	{
		Long serviceStart, serviceEnd, dbStart, dbEnd;
		
		serviceStart = System.currentTimeMillis();
		String query = String.format("select sum(CNT * height) as TotalHeight" +
				"	 from ((select LiftRides.LiftID as id, count(*) as CNT" +
				"    from LiftRides" +
				"	where LiftRides.SkierID = %d and LiftRides.Day = %d" +
				"	group by LiftRides.LiftID) as t" +
				"	left join Lifts on t.id = Lifts.id);", skierID, dayNum);
		
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Integer rval = -1;
		try {
			dbStart = System.currentTimeMillis();
			con = ConnectionPool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			dbEnd = System.currentTimeMillis();
			
			if (rs.next()) {
				rval = rs.getInt("TotalHeight");
			}
		} finally{
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
						serviceEnd - serviceStart, dbEnd - dbStart));
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
			@QueryParam("liftID") int liftID) throws SQLException, NamingException 
	{
		Long serviceStart, serviceEnd, dbStart, dbEnd;
		serviceStart =System.currentTimeMillis();
		String query = String.format("insert into LiftRides (ResortID, Day, SkierID, LiftID, Time) " +
				" value(%d, %d, %d, %d, %d);", resortID, dayNum, skierID, liftID, timestamp);

		Connection con = null;
		Statement stmt = null;
		try {
			dbStart = System.currentTimeMillis();
			
			con = ConnectionPool.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(query);
			
			dbEnd = System.currentTimeMillis();
		} finally{
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
						serviceEnd - serviceStart, dbEnd - dbStart));
		return Response.status(200).build();
    }         
}
