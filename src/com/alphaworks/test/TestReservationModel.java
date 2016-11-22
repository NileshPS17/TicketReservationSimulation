package com.alphaworks.test;

import java.util.Date;

import com.alphaworks.helpers.SeatType;
import com.alphaworks.model.StationModel;
import com.alphaworks.model.TrainModel;
import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;

public class TestReservationModel {
	public static void main(String... args) {
		Train t = TrainModel.getAll().get(0);
		Station src = StationModel.getAll().get(0), to = StationModel.getAll().get(1);
		SeatType st = SeatType.SL;
		System.out.println(new Date());
		
	}
}
