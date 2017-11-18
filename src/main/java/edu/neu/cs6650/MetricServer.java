package edu.neu.cs6650;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/metric")
public class MetricServer {
	@GET
	@Produces(MediaType.TEXT_PLAIN)           
	public String getMetric(
						@QueryParam("startMillis") Long startMillis,
						@QueryParam("endMillis") Long endMillis) {
		// dump current log into db
		Profiler.getInstance().dump();
		
		// retrive logs nad
		List<Log> logs = new ArrayList<Log>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
		    connection = (Connection) ConnectionPool.getConnection();
		    statement = connection.prepareStatement("insert into Logs values (?,?,?,?)");
		    statement.setTimestamp(1, new Timestamp(startMillis));
		    statement.setTimestamp(2, new Timestamp(endMillis));
		    rs = statement.executeQuery();
		    while (rs.next()) {
		    		logs.add(new Log(rs.getString(1), rs.getInt(1), rs.getInt(2)));
		    }
		}
		catch (Exception e) {
		    e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (statement != null) statement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return LogAnalyzer.analyze(logs, endMillis - startMillis);
	}
}

class LogAnalyzer {
	public static String analyze(List<Log> logs, long wallTime) {
		int errorCount = 0;
		ArrayList<Integer> dbLatencies = new ArrayList<Integer>();
		ArrayList<Integer> latencies = new ArrayList<Integer>();
		for (Log log: logs) {
			switch (log.getStatus()) {
				case "ERROR":
					errorCount += 1;
					break;
				case "SUCC":
					dbLatencies.add(log.getDbTime());
					latencies.add(log.getTotalTime());
					break;
			}
		}
		
		Collections.sort(latencies);
		Collections.sort(dbLatencies);
				
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(String.format("requests received: %d \n", latencies.size() + errorCount));
		stringBuilder.append(String.format("requests failed: %d \n", errorCount));
		stringBuilder.append(String.format("requests successed: %d \n", latencies));
		stringBuilder.append(String.format("successful req throughput: %d req/s\n", latencies.size()*1000/wallTime));
		
		long totalTime = 0;
		for (int latency: latencies) totalTime += latency;
		stringBuilder.append(String.format("service mean: %d ms\n", totalTime/latencies.size()));
		stringBuilder.append(String.format("service mdian: %d ms\n", latencies.get(latencies.size()/2)));
		stringBuilder.append(String.format("service 95th percentile: %d ms\n", latencies.get((int) (latencies.size()*0.95))));
		stringBuilder.append(String.format("service 99th percentile: %d ms\n", latencies.get((int) (latencies.size()*0.99))));
		
		totalTime = 0;
		for (int latency: dbLatencies) totalTime += latency;
		stringBuilder.append(String.format("db mean: %d ms\n", totalTime/dbLatencies.size()));
		stringBuilder.append(String.format("db mdian: %d ms\n", dbLatencies.get(dbLatencies.size()/2)));
		stringBuilder.append(String.format("db 95th percentile: %d ms\n", dbLatencies.get((int) (dbLatencies.size()*0.95))));
		stringBuilder.append(String.format("db 99th percentile: %d ms\n", dbLatencies.get((int) (dbLatencies.size()*0.99))));
		return stringBuilder.toString();
	}
}