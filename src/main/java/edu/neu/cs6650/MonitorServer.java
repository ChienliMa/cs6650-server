package edu.neu.cs6650;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/Monitor")
public class MonitorServer {
	@GET
	@Path("getMetric")
	@Produces(MediaType.TEXT_PLAIN)           
	public String getMetric(
						@QueryParam("startMillis") Long startMillis,
						@QueryParam("endMillis") Long endMillis) {
		List<String> logs = new ArrayList<String>();

		
		File cacheDir = new File("/tmp/CS6650Metrics");
		for (File timeDir: cacheDir.listFiles()) {
			long time = Long.parseLong(timeDir.getName());
			if (time > startMillis &&  time++ < endMillis) {
				for (File nodeLogs: timeDir.listFiles()) {
					FileReader fileReader = null;
					try {
						fileReader = new FileReader(nodeLogs);
						@SuppressWarnings("resource")
						BufferedReader bufferedReader = new BufferedReader(fileReader);
						String line;
						while ((line = bufferedReader.readLine()) != null) logs.add(line);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (fileReader != null)
							try {
								fileReader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
			}
		};
		return LogAnalyzer.analyze(logs, endMillis - startMillis);
	}
	
	@GET
	@Path("purgeCache")
	public void purgeCache(@QueryParam("startTime") Long time) {
		return ;
	}
	
	@POST
	@Path("/Logs")
	@Consumes(MediaType.TEXT_PLAIN)
	public void postText(
			@Context HttpServletRequest req,
			@QueryParam("time") Long time) {
		try {
			String absPath = String.format("/tmp/CS6650Metrics/%d/%s", time, req.getRemoteAddr());
			File logFile = new File(absPath);
			logFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(logFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}    
}

class LogAnalyzer {
	public static String analyze(List<String> logs, long wallTime) {
		int errorCount = 0;
		ArrayList<Integer> dbLatencies = new ArrayList<Integer>();
		ArrayList<Integer> latencies = new ArrayList<Integer>();
		for (String log:logs) {
			String[] cols = log.split(",");
			switch (cols[0]) {
				case "ERROR":
					break;
				case "SUCC":
					dbLatencies.add(Integer.parseInt(cols[0]));
					latencies.add(Integer.parseInt(cols[0]));
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