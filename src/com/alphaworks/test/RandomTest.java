package com.alphaworks.test;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;

public class RandomTest {
	
	public static void testCalendar() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, -10);
		System.out.println(c.getTime());
	}
	
	
	public static void main(String...strings) {
			
		(new FooDialog()).setVisible(true);
		
	}
	
	public static void p(String s){
		System.out.println(s);
	}
}


class FooDialog extends JDialog  {
	public FooDialog() {
		setLayout(new FlowLayout());
		add(new JButton("Hello!"));
		add(new JButton("World!"));
		setSize(new Dimension(400, 100));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
	}
}