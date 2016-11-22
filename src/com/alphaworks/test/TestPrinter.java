package com.alphaworks.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.alphaworks.helpers.ImageReplacedElementFactory;
import com.alphaworks.helpers.SeatType;
import com.alphaworks.helpers.TicketPrinter;
import com.alphaworks.model.StationModel;
import com.alphaworks.wrappers.Person;

public class TestPrinter {
	public static void main(String...strings) throws Exception {
		TicketPrinter tp = new TicketPrinter();
		List<Person> list = Arrays.asList(new Person("123456789", "John Wallock", 50, 12),
					  new Person("827348933", "Mark Wallberg", 30, 13),
					  new Person("832843788", "William", 50, 45),
					  new Person("123393284", "Fernandes", 67, 34),
					  new Person("123981239", "Maverick", 11, 20));
		
		tp.setTicketNo("12345678910");
		tp.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("12/11/2016"));
		tp.setPath(StationModel.getStationByID(1), StationModel.getStationByID(5));
		tp.addRows(list);
		tp.setTotal(1200);
		tp.setSeatType(SeatType.AC);
		String str = tp.build();
		OutputStream os = new FileOutputStream(new File("/home/nilesh/Desktop/ticket.pdf"));
		ITextRenderer r = new ITextRenderer();
		r.getSharedContext().setReplacedElementFactory(
				new ImageReplacedElementFactory(
						r.getSharedContext().getReplacedElementFactory()
						)
				);
		r.setDocumentFromString(str);
		r.layout();
		r.createPDF(os);
		os.close();
		
		
		
	}
}
