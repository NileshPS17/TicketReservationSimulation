package com.alphaworks.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alphaworks.model.TrainModel;
import com.alphaworks.wrappers.Train;

public class TestTrain {
	public static void main(String...strings) throws Exception {
		
		for(Train t : TrainModel.getAll()) {
			for(int i=0; i<7; ++i) {
				Date d= new SimpleDateFormat("dd/mm/yyyy").parse((i + 9) + "/10/2016");	
				String day = new SimpleDateFormat("EEE").format(d);
				System.out.println(t.getTrainName() + "\t" + day + "\t" + t.isRunningOnDate(d));
			}
		}
	}
}
