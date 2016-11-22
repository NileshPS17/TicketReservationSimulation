package com.alphaworks.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;
import com.alphaworks.wrappers.Train.Rate;

/**
 * A helper class for any {@link com.alphaworks.wrappers.Train Train } related queries.<br />
 * Abstracts the underlying database operations.
 * @author nilesh
 *
 */

public class TrainModel {
	
	/**
	 * Used to cache the list of trains fetched from the database.
	 */
	private static List<Train> mTrainList = null;
	
	private TrainModel() {
		//Restrict usage via static methods only
	}
	
	/**
	 * Get the cache list.
	 * @return {@link java.util.List List}<{@link com.alphaworks.wrappers.Train Train}>
	 */
	public static List<Train> getAll() {
		return mTrainList;
	}
	
	/**
	 * Fetch all trains running between the given two stations on the specified date .
	 * @param srcStationID ID corresponding to the source {@link com.alphaworks.wrappers.Station Station} .
	 * @param dstStationID ID corresponding to the destination {@link com.alphaworks.wrappers.Station Station} .
	 * @param d 		   Date of journey.	
	 * @return null, if something went wrong. Watch the stderr output for the stackTrace.
	 */
	public static List<SearchResult> getTrainsBetweenStations(int srcStationID ,int dstStationID, Date d) {
		if(srcStationID == dstStationID) {
			return null;
		}
		SimpleDateFormat sdf;
		try {
			sdf = new SimpleDateFormat("EEE");
			String day = sdf.format(d).toLowerCase();
			PreparedStatement st  = Database.getInstance().prepareStatement(
					"SELECT  t.train_id, t.name, r1.dep_time , r2.arr_time, r1.stop_no src_stop_no, r2.stop_no dest_stop_no " +
					"FROM route_stops r1, route_stops r2, trains t " +
					"where r1.station_id = ? AND " +
					"r2.station_id = ? AND " +
                    "r2.stop_no > r1.stop_no AND " +
					"r1.train_id = r2.train_id AND " +
                    "t.train_id = r1.train_id AND " + 
                     day + " = TRUE"
                     );
			st.setInt(1, srcStationID);
			st.setInt(2, dstStationID);
			ResultSet rs = st.executeQuery();
			List<SearchResult> list = new ArrayList<SearchResult>();
			while(rs.next()) {
				int srcStopNo = rs.getInt("src_stop_no");
				int destStopNo = rs.getInt("dest_stop_no");
				int trainNo = rs.getInt("train_id");
				ResultSet rs2 = Database.getInstance().createStatement().executeQuery("SELECT SUM(distance) dist FROM route_stops WHERE train_id = "+ trainNo + " AND stop_no > " + srcStopNo + " AND stop_no <= " + destStopNo);
				float distance = 0;
				if(rs2.next()) {
					distance = rs2.getFloat("dist");
				}
				else {
					throw new IllegalStateException("No distance!");
				}
				
				list.add(
						new SearchResult
						(
								rs.getInt("train_id"),
								rs.getString("name"),
								rs.getString("dep_time"),
								rs.getString("arr_time"),
								rs.getInt("src_stop_no"),
								rs.getInt("dest_stop_no"),
								distance
						)
					);
				
			}
			return list;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the schedule of a specific train.
	 * @param trainNo {@link com.alphaworks.wrappers.Train Train} 
	 * @return {@link java.util.List List}<{@link com.alphaworks.model.TrainModel.ScheduleStop ScheduleStop} >
	 */
	public static List<ScheduleStop> getSchedule(int trainNo) {
		try {
			Statement st = Database.getInstance().createStatement();
			ResultSet rs = st.executeQuery("SELECT CONCAT(s.code, ' - ', s.name) name, r.stop_no, r.arr_time, r.dep_time, r.distance " + 
											"FROM stations s JOIN route_stops r ON s.station_id = r.station_id WHERE " +
											"r.train_id = " + trainNo + " ORDER BY r.stop_no");
			List<ScheduleStop> list = new ArrayList<>();
			while(rs.next()) {
				ScheduleStop ss = new ScheduleStop(rs.getString("name"),
						rs.getString("arr_time"), rs.getString("dep_time"), rs.getFloat("distance"));
				list.add(ss);
			}
			return Collections.unmodifiableList(list);
					                      
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the current status of the train.
	 * @param t The train object whose status is to be checked.
	 * @return {@link com.alphaworks.model.TrainModel.Status Status}
	 */
	
	public static Status getStatus(Train t) {
		Status mStatus = new Status();
		if(t == null)
			throw new IllegalArgumentException();
		
		if(!t.isRunningOnDate(new Date())) {
			mStatus.code = Status.NOT_RUNNING;
			return mStatus;
		}
		Date today = new Date();
		try {
			Statement st = Database.getInstance().createStatement();
			String curTime = new SimpleDateFormat("HH:mm:ss").format(today.getTime());
			ResultSet rs;
			rs = st.executeQuery("SELECT dep_time, station_id FROM route_stops WHERE train_id="+t.getTrainNo() + " AND arr_time IS NULL AND dep_time > \'" +curTime + "\'" );
			//If the train has not left the station just yet.
			while(rs.next()) {
				String rsdate = rs.getString("dep_time");	
				mStatus.code = Status.NOT_STARTED;
				mStatus.lastStation = StationModel.getStationByID(rs.getInt("station_id"));
				mStatus.atTime = rsdate;
				return mStatus;
			}
			//if the train has completed its journey
			rs = st.executeQuery("SELECT arr_time, station_id FROM route_stops WHERE train_id = " + t.getTrainNo() + " AND dep_time IS NULL AND arr_time < \'" + curTime + "\'");
			while(rs.next()){
				String rsdate = rs.getString("arr_time");
				mStatus.code = Status.FINISHED;
				mStatus.lastStation = StationModel.getStationByID(rs.getInt("station_id"));
				mStatus.atTime = rsdate;
				return mStatus;
			}
			//between stations
			rs = st.executeQuery("SELECT * FROM route_stops WHERE dep_time IS NOT NULL AND train_id = " + t.getTrainNo() + " AND dep_time <= \'" + curTime + "\'  ORDER BY stop_no DESC LIMIT 1");
			while(rs.next()) {
				mStatus.code = Status.LEFT_STATION;
				mStatus.lastStation = StationModel.getStationByID(rs.getInt("station_id"));
				mStatus.atTime = rs.getString("dep_time");
				break;
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return mStatus;
	}
	
	/**
	 * Init the model. <br />
	 * Fetch the list of trains from the database and cache it.
	 */
	private static void init() {
		try {
			Statement st = Database.getInstance().createStatement();
			ResultSet rs = st.executeQuery("SELECT train_id, name, sun, mon, tue, wed, thu, fri, sat " +
										   "FROM trains t");
			mTrainList = new ArrayList<>();
			while(rs.next()) {
				Train t = new Train(rs.getInt(1), rs.getString(2));
				int a = 0;
				for(int i=3; i<10; ++i) {
					a = ( a << 1) | rs.getInt(i);
				}
				t.setRunningDays(a);
			
				ResultSet rs2 = Database.getInstance().createStatement().executeQuery(
								String.format("SELECT * FROM seating_capacity WHERE train_id = %d ", t.getTrainNo())
								);
				while(rs2.next()) {
					String type = rs2.getString("seat_type");
					float charge = rs2.getFloat("min_charge"), rate =  rs2.getFloat("rate");
					int no = rs2.getInt("no_of_seats");
					t.getSeatingCapacity().put(type , new Rate(charge, rate, no));
				}
				
				mTrainList.add(t);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve a {@link com.alphaworks.wrappers.Train Train} object that matches the given id.
	 * @param id ID of the train.
	 * @return null, if the ID was invalid.
	 */
	
	public static Train getTrainByID(int id) {
		for(Train t: mTrainList)
			if(t.getTrainNo() == id)
				return t;
		
		return null;
	}
	
	
	static {
		init();
	}
	
	/**
	 * Holds the details of an intermediate station.
	 * @author nilesh
	 *
	 */
	
	public static class ScheduleStop {
		public String stationName, arrivalTime, departureTime;
		public float distance;
		public ScheduleStop(String stationName, String arrivalTime, String departureTime, float distance) {
			super();
			this.stationName = stationName;
			this.arrivalTime = (arrivalTime == null)?"N/A":arrivalTime;
			this.departureTime = (departureTime == null)?"N/A":departureTime;
			this.distance = distance;
		}
		
	}
	
	
	/**
	 * Wraps the result of search for trains b/w stations into a single object.
	 * @author nilesh
	 *
	 */
	public static class SearchResult {
		public String trainName, arrTime, depTime;
		public int trainNo;
		public float distance;
		public int srcStopNo, destStopNo;
		public SearchResult(int trainNo, String trainName, String destTime, String arrTime,int srcStopNo, int destStopNo , float mDist) {
			super();
			this.trainName = trainName;
			this.arrTime = arrTime;
			this.depTime = destTime;
			this.trainNo = trainNo;
			this.distance = mDist;
			this.srcStopNo = srcStopNo;
			this.destStopNo = destStopNo;
		
		}
		
	}
	
	/**
	 * Signals the current status of the train.
	 * @author nilesh
	 *
	 */
	public static class Status {
		public static final int NOT_RUNNING = 0, LEFT_STATION = 1, NOT_STARTED = 2, FINISHED = 3;
		public int code = NOT_RUNNING;
		public Station lastStation = null;
		public String atTime = "";
		public Status(int code, Station lastStation, String atTime) {
			super();
			this.code = code;
			this.lastStation = lastStation;
			this.atTime = atTime;
		}
		
		public Status() { 
			//pass
		}
		
		
	}
}
