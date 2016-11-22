package com.alphaworks.helpers;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import com.qt.datapicker.DatePicker;

public class DateTextField extends JTextField implements Observer {
	
	private static final long serialVersionUID = 3714665725874500427L;
	
	public DateTextField(int columns) {
		super(columns);
	}
	
	public DateTextField() {
		super();
	}
    @Override
	public void update(Observable o, Object arg) {
        Calendar calendar = (Calendar) arg;
        DatePicker dp = (DatePicker) o;	
        setText(dp.formatDate(calendar, "dd/MM/yyyy"));
    }
}
