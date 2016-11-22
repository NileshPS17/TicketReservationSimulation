package com.alphaworks.wrappers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for holding the details of a train.
 * @author nilesh
 *
 */
public class Train {
	/**
	 * Train no. Unique to every train.
	 */
	private int trainNo;
	
	/**
	 * The name of the train. Eg. Rajdhani Express
	 */
	private String trainName;
	
	/**
	 * Maps a seat type to its relevant details.
	 */
	private Map<String, Rate> seats;
	
	/**
	 * Uses individual bits to store the weekly information about trains.
	 * Bit 7 (0 indexed) from LSB is Sunday and LSB is saturday.
	 */
	
	private int daysAvailable = 0;
	
	public Train(int trainNo, String trainName) {
		super();
		this.trainNo = trainNo;
		this.trainName = trainName;
		seats = new HashMap<String, Rate>();
		
	}
	
	public Map<String, Rate> getSeatingCapacity() {
		return seats;
	}
	public int getTrainNo() {
		return trainNo;
	}
	public void setTrainNo(int trainNo) {
		this.trainNo = trainNo;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	
	public void setRunningDays(int a) {
		this.daysAvailable  = a;
	}
	
	public int getRunningDays() { return this.daysAvailable; }
	
	/**
	 * Return true if train is running on that particular day of the week.
	 * @param d sun(0), mon(1) .. sat(6)
	 * @return boolean
	 */
	public boolean isRunningOnDay(int d) {
		if(d < 0 || d > 6)
			throw new IllegalArgumentException("Invalid day " + d);
		int offset = 6-d;
		return  (daysAvailable & ( 1 << offset)) == (1 << offset);
	}
	public boolean isRunningOnDate(Date d) {
		if( d == null)
			throw new IllegalArgumentException();
		String day  = new SimpleDateFormat("EEE").format(d).toLowerCase();
		int offset = 0;
		switch(day) {
		case "sat" :
			offset = 0;
			break;
		case "fri" :
			offset = 1;
			break;
		case "thu" : 
			offset = 2;
			break;
		case "wed" : 
			offset = 3;
			break;
		case "tue" : 
			offset = 4;
			break;
		case "mon" :
			offset = 5;
			break;
		case "sun" : 
			offset = 6;
			break;
		default:
			throw new RuntimeException("Invalid day!" + day);
		}
		
		return (daysAvailable &  ( 1 << offset)) == (1<<offset);
	}
	@Override
	public String toString() {
		return trainNo + " - " + trainName;
	}
	
	@Override
	public boolean equals(Object t) {
		if(t != null && t instanceof Train)
			return ((Train)t).getTrainNo() == trainNo;
		
		return false;
	}
	
	/**
	 * Holds details like the minimum rate, rate per km and capacity .
	 * This is usually mapped with a single seat type.
	 * @author nilesh
	 *
	 */
	public static class Rate {
		public float min, perKm;
		public int capacity;

		public Rate(float min, float perKm) {
			this.min = min;
			this.perKm = perKm;
		}
		
		public Rate(float min, float perKm, int c) {
			this.min = min;
			this.perKm = perKm;
			capacity = c;
		}
		
		
	}
	
}

