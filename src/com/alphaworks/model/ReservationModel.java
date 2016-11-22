package com.alphaworks.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.alphaworks.helpers.SeatType;
import com.alphaworks.model.TrainModel.SearchResult;
import com.alphaworks.wrappers.Person;
import com.alphaworks.wrappers.Reservation;
import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;

public class ReservationModel {

	/**
	 * Tries to place a reservation as per the details given.
	 * @param arg - {@link com.alphaworks.wrappers.Reservation Reservation }
	 * @return True, if reservation request was successful.
	 * @throws Exception 
	 */
	public static boolean reserve(Reservation arg) throws Exception {
		Train train = arg.getTrain();
		Station from  = arg.getFrom(), to = arg.getTo();
		Date on = arg.getDate();
		SeatType type = arg.getSeatType();
		
		if(train == null || from == null || to == null || on == null || type == null)
			throw new IllegalArgumentException("Please provide all arguments");
		
		List<SearchResult> t = TrainModel.getTrainsBetweenStations(from.getID(), to.getID(), on);
		SearchResult mResult = null;
		if( t != null) {
			for(SearchResult sr : t) {
				if(sr.trainNo == train.getTrainNo()) {
					mResult = sr;
					break;
				}
			}
		}
		
		if ( mResult == null)
			throw new Exception("Sorry! " + train.getTrainName() + " does not take the specified route.");
		
		if(on.before(new Date()))
				throw new IllegalArgumentException("Please provide a valid date!");
		
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, 90); // add 90 days
		String date = new SimpleDateFormat("dd/MM/yyyy").format(on);
		
		if(on.after(c.getTime())) {
			throw new Exception("Sorry! Reservations for " + date + " is not open yet!");
		}
		try {
			PreparedStatement ps = Database.getInstance().prepareStatement(
					" SELECT r.reservation_id, sr.seat_no  FROM 																" + 
					" reservation r, seats_reserved sr, route_stops rs1, route_stops rs2, route_stops rs3, route_stops rs4 	" + 
					" where r.for_date = str_to_date( ? , '%d/%m/%Y') AND														" + 
					" r.seat_type = ? AND																					" + 
					" r.train_id = ? AND																					" +
					" r.reservation_id = sr.reservation_id AND																" + 
					" rs1.train_id = r.train_id AND															" + 
					" rs2.train_id = r.train_id AND															" + 
					" rs3.train_id = r.train_id AND															" + 
					" rs4.train_id = r.train_id AND															" + 
					" rs1.station_id = ? 		AND 														" +  //src station
					" rs2.station_id = r.from_station AND													" + 
					" rs3.station_id = r.to_station AND 													" + 
					" rs4.station_id = ?			 AND 													" +  //destination
					" (																						" + 
					"     ( rs2.stop_no <= rs1.stop_no AND rs3.stop_no > rs1.stop_no) 						" + 
					"     OR 																				" + 	
					"     ( rs2.stop_no >= rs1.stop_no AND rs2.stop_no < rs4.stop_no )						" + 
					" )	"																		
				);
			
			ps.setString(1, date);
			ps.setString(2, type.toString());
			ps.setInt(3, train.getTrainNo());
			ps.setInt(4, from.getID());
			ps.setInt(5, to.getID());
			
			ResultSet res = ps.executeQuery();
			HashSet<Integer> mSet = new HashSet<>();
			while(res.next()) {
				mSet.add(res.getInt("seat_no"));
			}
			Train.Rate rate = train.getSeatingCapacity().get(type.name());
			if(rate.capacity - mSet.size() < arg.getList().size()) {
				setErrorMessage("Not enough seats are available!");
				return false;
			}
			//else, allocate some free seats
			Iterator<Person> it = arg.getList().iterator();
			for(int i=1; i<=rate.capacity && it.hasNext(); ++i) {
				if(!mSet.contains(i)) {
					it.next().setSeatNo(i);
				}
			}
			
			float total_amount = Math.max(rate.min, mResult.distance * rate.perKm * arg.getList().size() );
			arg.setTotal(total_amount);
			//begin transaction
			Database.getInstance().setAutoCommit(false);
			Statement st = Database.getInstance().createStatement();
			String query = String.format("INSERT INTO reservation(train_id, from_station, to_station, for_date, no_of_seats, seat_type, total_amount) " +
										 " VALUES(%d, %d, %d, str_to_date('%s', '%%d/%%m/%%Y'), %d, '%s', %f)",
										 train.getTrainNo(), 
										 from.getID(), 
										 to.getID(),
										 date, /** formatted in dd/MM/yyy */
										 arg.getList().size(), 
										 type.name(),
										 total_amount);
			int rowsAffected = st.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
			if(rowsAffected == 0)
				throw new Exception("INSERTION into reservation table failed! ");
			
			int lastInsertId = -1;
			ResultSet genKeys = st.getGeneratedKeys();
			while(genKeys.next()) {
				lastInsertId = genKeys.getInt(1);
				break;
			}
			
			if(lastInsertId == -1)
				throw new Exception("LAST_INSERT_ID() fetch failed!");
			
			arg.setReservationID(lastInsertId);
			
			String template = "INSERT INTO seats_reserved(reservation_id, person_id, name, age, seat_no) VALUES  (%d, %d, '%s', %d, %d)";
			rowsAffected = 0;
			for(Person p : arg.getList()) {
				rowsAffected += st.executeUpdate(template.format(template, lastInsertId,  Integer.parseInt(p.getId()), p.getName(), p.getAge(), p.getSeatNo()));
			}
			if(rowsAffected < arg.getList().size()) 
				throw new Exception("Failed to insert all reserved seats!");
			
			Database.getInstance().commit();
			//end of transaction
			Database.getInstance().setAutoCommit(true);
		}
		catch(SQLException e) {
			e.printStackTrace();
			System.out.println(e);
			if(Database.getInstance().getAutoCommit() == false) {
				Database.getInstance().rollback();
				Database.getInstance().setAutoCommit(true);
			}
			setErrorMessage(e.getMessage());
			return false;
		}
																		
		return true;
	}
	
	/**
	 * Get the reservation details corresponding to the ID passed as parameter.
	 * @param id  The reservation id shown in the ticket.
	 * @param date Day for which the reservation was made.
	 * @return {@link com.alphaworks.wrappers.Reservation Reservation}
	 */
	
	public static Reservation getReservation(int id, Date date) {
		if( id < 0 || date == null)
			throw new IllegalArgumentException();
		Reservation mRes = null;
		try {
			Statement st = Database.getInstance().createStatement();
			String mDate = new SimpleDateFormat("dd/MM/yyyy").format(date);
			ResultSet rs = st.executeQuery("SELECT * FROM reservation r, seats_reserved s "
										 + " WHERE r.reservation_id = " + id + " AND r.for_date = str_to_date(\'" + mDate + "\', \'%d/%m/%Y\')" 
										 + " AND r.reservation_id = s.reservation_id" );
			while(rs.next()) {
				if( mRes == null) {
					mRes = new Reservation();
					mRes.setDate(date);
					mRes.setReservationID(id);
					mRes.setSeatType(SeatType.valueOf(rs.getString("seat_type")));
					mRes.setTrain(TrainModel.getTrainByID(rs.getInt("train_id")));
					mRes.setFrom(StationModel.getStationByID(rs.getInt("from_station")));
					mRes.setTo(StationModel.getStationByID(rs.getInt("to_station")));
					mRes.setTotal(rs.getFloat("total_amount"));
				}
				
				Person p = new Person(rs.getString("person_id"), rs.getString("name"));
				p.setSeatNo(rs.getInt("seat_no"));
				p.setAge(rs.getInt("age"));
				mRes.addPerson(p);
			}
			
		}
		catch(Exception e) {
			mRes = null;
		}
		
		return mRes;
	}
	
	/**
	 * Cancel the reservation.
	 * @param id The reservation id shown in the ticket.
	 * @param date The date of journey.
	 * @return boolean  True, if the ticket was successfully cancelled.
	 */
	public static boolean cancelReservation(int id, Date date) throws Exception {
		if(date.before(new Date()))
			throw new Exception("Invalid date!");
		//Find the reservation

		PreparedStatement ps = Database.getInstance().prepareStatement(
				"SELECT * FROM reservation WHERE reservation_id = ? AND for_date = ?" );
		
		String fmtDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		
		
		ps.setInt(1, id);
		ps.setString(2, fmtDate);
		
		ResultSet rs = ps.executeQuery();
		boolean found = false;
		while(rs.next()) {
			found = true;
			break;
		}
		
		if(!found)
			throw new Exception("Sorry! No matching reservation found in the database.");
		

		//Tickets cannot be cancelled on the day of journey.
		if(date.before(new Date())) {
			return false;
		}
		
		Statement st = Database.getInstance().createStatement();
		int numRows = st.executeUpdate("DELETE FROM reservation WHERE reservation_id=" + id);
		if(numRows == 0)
			throw new Exception("Something went wrong!");
		
		
		return true;
	}
	
	
	/**
	 * Holds the description of the last known error.
	 */
	
	private static String errMsg = "";
	
	/**
	 * Return a user-friendly description of the last known error.
	 * This method cannot be used more than once to retrieve the detail of same error.
	 * @return String - String
	 */
	public static String getErrorMessage() {
		String msg = errMsg;
		errMsg = "";
		return msg;
	}
	
	/**
	 * Set the error description.
	 * @param s
	 */
	private static void setErrorMessage(String s) {
		//System.out.println(s);
		errMsg = s;
	}

}
