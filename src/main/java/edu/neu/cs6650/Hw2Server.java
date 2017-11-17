package edu.neu.cs6650;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;

import javax.ws.rs.QueryParam;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.net.ssl.HandshakeCompletedEvent;


@Path("/resort")
public class Hw2Server {
	@GET
	@Produces(MediaType.TEXT_PLAIN)           
	public String test() {
		return "RESORT";
	}
	
	
	@GET
	@Path("/myvert")
	@Produces(MediaType.TEXT_PLAIN)           
	public String getStatus(
			@QueryParam("skierID") int skierID,
			@QueryParam("dayNum") int dayNum) 
	{
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
			con = ConnectionPool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			if (rs.next()) {
				rval = rs.getInt("TotalHeight");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}finally{
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(con != null) con.close();
			} catch (SQLException e) {
				System.out.println(e.toString());
			}
		}
		return rval.toString();
	}
	
	
	@POST
	@Path("/load")
	@Produces(MediaType.TEXT_PLAIN)
	public void postText(
			@QueryParam("resortID") int resortID,
			@QueryParam("dayNum") int dayNum,
			@QueryParam("timestamp") int timestamp,
			@QueryParam("skierID") int skierID,
			@QueryParam("liftID") int liftID) 
	{
		String query = String.format("insert into LiftRides (ResortID, Day, SkierID, LiftID, Time) " +
				" value(%d, %d, %d, %d, %d);", resortID, dayNum, skierID, liftID, timestamp);

		Connection con = null;
		Statement stmt = null;
		try {
			con = ConnectionPool.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println(e.toString());
		} catch (NamingException e) {
			System.out.println(e.toString());
		}finally{
				try {
					if(stmt != null) stmt.close();
					if(con != null) con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return;
    }         
}
