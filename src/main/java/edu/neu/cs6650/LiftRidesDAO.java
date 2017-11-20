package edu.neu.cs6650;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;

public class LiftRidesDAO {
	private List<LiftRides> liftRides;
	private static LiftRidesDAO instance = null;
	private int batchSize = 10;
	
	public LiftRidesDAO() {
		this.liftRides = new ArrayList<LiftRides>();
	}
	
	public static LiftRidesDAO getInstance() {
		if (instance == null) {
			instance = new LiftRidesDAO();
		}
		return instance;
	}
	
	public void insertLiftRide(LiftRides liftRide) throws SQLException, NamingException {
		List<LiftRides> batch = null;
		synchronized (this) {
			if (this.liftRides.size() >= batchSize) {
				 batch = liftRides;
				 liftRides = new ArrayList<LiftRides>();
			}
			liftRides.add(liftRide);
		}
		
		if (batch == null) return;
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = ConnectionPool.getConnection();
			stmt = con.prepareStatement("insert into LiftRides (ResortID, Day, SkierID, LiftID, Time) " +
													" value(?, ?, ?, ?, ?)");
			for(LiftRides ride: batch) {
				stmt.setInt(1, ride.getResortID());
				stmt.setInt(2, ride.getDay());
				stmt.setInt(3, ride.getSkierID());
				stmt.setInt(4, ride.getLiftID());
				stmt.setInt(5, ride.getTime());
				stmt.addBatch();
			}
			stmt.executeBatch();
		} finally{
			try {
				if(stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
			try {
				if(con != null) con.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
		}
	}
	
	public int getVert(int id, int day) throws SQLException, NamingException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int rval = -1;
		String query = String.format("select sum(CNT * height) as TotalHeight" +
				"	 from ((select LiftRides.LiftID as id, count(*) as CNT" +
				"    from LiftRides" +
				"	where LiftRides.SkierID = %d and LiftRides.Day = %d" +
				"	group by LiftRides.LiftID) as t" +
				"	left join Lifts on t.id = Lifts.id);", id, day);
		
		try {
			con = ConnectionPool.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				rval = rs.getInt("TotalHeight");
			}
			return rval;
		} finally {
			try {
				if(rs != null) rs.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
			try {
				if(stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
			try {
				if(con != null) con.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
		}
	}
	
}
