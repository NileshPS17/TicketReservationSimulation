package com.alphaworks.helpers;

/**
 * Used to identify different seat types available in a train. 
 * A particular train may acoomodate all kinds of seating, or just any one of it's non empy subset.
 * @author nilesh
 *
 */
public enum SeatType {
	SL("SL"), CC("CC"), AC("AC");
	SeatType(String type) {
		str = type;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	
	private String str;
	
}
