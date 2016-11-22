package com.alphaworks.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alphaworks.wrappers.Station;

/**
 * Handles any {@link com.alphaworks.wrappers.Station Station} related queries.
 * @author nilesh
 *
 */
public class StationModel  {
	/**
	 * Used to cache the list of all stations.
	 */
	private static List<Station> mStationsList = new ArrayList<Station>();
	
	/**
	 * Retrieve all stations.
	 * @return {@link java.util.List List}<{@link com.alphaworks.wrappers.Station Station}>
	 */
	public static List<Station> getAll() {
		return mStationsList;
	}

	
	/**
	 * Bootstrap the model. Fetch all available stations from the database and caches it for future use.
	 * This will result in faster and smoother user interaction.
	 */
	private static void init() {
		try {
			Statement st = Database.getInstance().createStatement();
			ResultSet rs = st.executeQuery("SELECT station_id, code, name FROM stations");
			mStationsList = new ArrayList<>();
			while(rs.next()) {
				mStationsList.add(new Station(rs.getInt(1), rs.getString(2),rs.getString(3)));
			}
			st.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	/**
	 * Get the {@link com.alphaworks.wrappers.Station Station} corresponding to the id passed as parameter.
	 * @param id The station id.
	 * @return Null , if no such station exist.
	 */
	public static Station getStationByID(int id) {
		for(Station st : mStationsList){
			if(st.getID() == id)
				return st;
		}
		return null;
	}
	
	static {
		init();
	}
}
