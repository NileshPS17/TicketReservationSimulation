package com.alphaworks.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alphaworks.R;
import com.alphaworks.wrappers.Person;
import com.alphaworks.wrappers.Reservation;
import com.alphaworks.wrappers.Station;


/**
 * A helper class for generating PDF document of an input {@link com.alphaworks.wrappers.Reservation Reservation }.
 * @author nilesh
 *
 */
public class TicketPrinter {
	/**
	 * Placeholders from the template file where the information about the ticket is to be substituted.
	 */
	private static final String TICKET 	= "{{ticket}}";
	private static final String DATE	= "{{date}}";
	private static final String TO		= "{{to}}";
	private static final String FROM	= "{{from}}";
	private static final String ROW		= "{{row}}";
	private static final String TOTAL   = "{{total}}";
	
	private List<Person> mPersonList;
	private StringBuilder mBuilder = new StringBuilder();
	
	private String ticket;
	private Date date;
	private SeatType mSeatType;
	private Station from, to;
	private float total = -1;
	
	public TicketPrinter() {
		mPersonList = new ArrayList<Person>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(R.loadFile("ticket_template.html")));
			String str;
			while((str = br.readLine()) != null) {
				mBuilder.append(str);
			}
			br.close();
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public void setTicketNo(String ticketNo) {
		ticket = ticketNo;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setPath(Station fromStation, Station toStation) {
		this.from = fromStation;
		this.to = toStation;
	}
	
	public void setSeatType(SeatType type) {
		mSeatType = type;
	}
	
	public void addRow(Person p) {
		mPersonList.add(p);
	}
	public void addRows(List<Person> p) {
		if(p != null) {
			for(Person _p : p) {
				mPersonList.add(_p);
			}
		}
	}
	public void removeRow(Person p) {
		mPersonList.remove(p);
	}
	
	public void setTotal(float total) {
		this.total = total;
	}
	
	public String build() {
		validate();
		int indexOfTicket = mBuilder.indexOf(TICKET);
		mBuilder.replace(indexOfTicket, indexOfTicket + TICKET.length(), ticket);
		int indexOfDate = mBuilder.indexOf(DATE);
		mBuilder.replace(indexOfDate, indexOfDate  + DATE.length(), new SimpleDateFormat("dd/MM/yyyy").format(date));
		int indexOfFrom = mBuilder.indexOf(FROM);
		mBuilder.replace(indexOfFrom, indexOfFrom + FROM.length(), from.toString());
		int indexOfTo = mBuilder.indexOf(TO);
		mBuilder.replace(indexOfTo, indexOfTo + TO.length(), to.toString());
		String row = buildRows();
		int indexOfRow =mBuilder.indexOf(ROW);
		mBuilder.replace(indexOfRow, indexOfRow + ROW.length(), row);
		int indexOfTotal = mBuilder.indexOf(TOTAL);
		mBuilder.replace(indexOfTotal, indexOfTotal + TOTAL.length(), "Rs. " + String.valueOf(total) + "/-");
		return mBuilder.toString();
	}
	
	private String buildRows() {
		StringBuilder br = new StringBuilder();
		int c = 0;
		for(Person p : mPersonList) {
			++c;
			br.append("<tr>" +
						"<td>" + c + "</td>" +
						"<td>" + p.getName() + "</td>" + 
						"<td>" + p.getId() + "</td>" + 
						"<td>" + p.getAge() + "</td>"  +
						"<td>" + mSeatType + " " + p.getSeatNo() + "</td>" +
					  "</tr>" );
		}
		
		return br.toString();
	}
	
	private void validate() {
		if(!Helper.isValidTicketNo(ticket) 		|| 
			date == null		|| 
			mSeatType == null 	|| 
			from == null 		||
			to == null			||
			total <= 0 ) {
					throw new RuntimeException("Please fill in all details before build!");
				}
		
	}
	
	
	public static TicketPrinter fromReservation(Reservation res) {
		
		TicketPrinter p;
		try {
			p = new TicketPrinter();
			p.setDate(res.getDate());
			p.setPath(res.getFrom(), res.getTo());
			p.setSeatType(res.getSeatType());
			p.setTicketNo(String.valueOf(res.getReservationID()));
			p.setTotal(res.getTotal());
			p.addRows(res.getList());
			
		}
		catch(Exception e) {
			e.printStackTrace();
			p = null;
		}
		
		return p;
	}
	
	
}
