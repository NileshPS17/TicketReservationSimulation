package com.alphaworks.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import com.alphaworks.model.StationModel;
import com.alphaworks.model.TrainModel;
import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;

public class Helper {
	private Helper() {
		
	}
	
	public static boolean isValidSeatType(String type) {
		return (type != null) &&
				( type.equals("SL") || type.equals("CC") || type.equals("AC"));
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}
	
	public static void populateStationsCombo(JComboBox<Station> combo) {
		for(Station st : StationModel.getAll()) {
			combo.addItem(st);
		}
	}
	
	
	public static void populateTrainCombo(JComboBox<Train> c) {
		for(Train t: TrainModel.getAll())
			c.addItem(t);
	}
	public static enum Day {
		SUN(6), MON(5), TUE(4), WED(3), THU(2), FRI(1), SAT(0);
		public int offset;
		Day(int a) {
			offset = a;
		}
		
		int set(int a) {
			return a | (1 << offset);
		}
		
		boolean isSet(int a) {
			return (a & (1 << offset)) == 1;
		}
		
	}
	
	public static boolean isValidTicketNo(String no) {
		if( no == null || no.trim().equals(""))
			return false;
		
		Pattern p = Pattern.compile("^[0-9]{2,}$");
		Matcher m = p.matcher(no);
		return m.matches();
	}
}
