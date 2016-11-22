package com.alphaworks.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.alphaworks.helpers.SeatType;


/**
 * A wrapper class for reservation details.
 * @author nilesh
 *
 */
public class Reservation {
	/**
	 * Station object corresponding to the source and destination station.
	 */
	private Station from, to;
	
	/**
	 * Date of the journey. 
	 */
	private Date date;
	
	/**
	 * List of persons for whom the reservation was made.
	 */
	private List<Person> mList = new ArrayList<>();
	
	/**
	 * Train 
	 */
	private Train train;
	
	/**
	 * Type of compartment.
	 */
	private SeatType seatType;
	
	/**
	 * A unique id used to identify this reservation.
	 */
	private int reservationID;
	
	/**
	 * Total amount as shown in the ticket.
	 */
	private float total;
	
	public float getTotal() {
		return total;
	}
	public void setTotal(float total) {
		this.total = total;
	}
	public int getReservationID() {
		return reservationID;
	}
	public void setReservationID(int reservationID) {
		this.reservationID = reservationID;
	}
	public Station getFrom() {
		return from;
	}
	public void setFrom(Station from) {
		this.from = from;
	}
	public Station getTo() {
		return to;
	}
	public void setTo(Station to) {
		this.to = to;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Train getTrain() {
		return train;
	}
	public void setTrain(Train train) {
		this.train = train;
	}
	
	public void addPerson(Person p) {
		for(Person _p : mList) {
			if(_p.getId().equals(p.getId()))
				throw new IllegalArgumentException("Duplicate ID");
		}
		
		mList.add(p);
	}
	
	public void deletePerson(Person p) {
		mList.remove(p);
	}
	
	public List<Person> getList() {
		return Collections.unmodifiableList(mList);
	}
	
	public SeatType getSeatType() {
		return seatType;
	}
	
	public void setSeatType(SeatType seatType) {
		this.seatType = seatType;
	}
	
	
	
}
