package com.alphaworks.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class SchedulePanel extends JPanel {
	
	private JButton btnSubmit;
	private JComboBox comboBox;
	private JTable table;
	
	public SchedulePanel() {

		bootstrapLayout();
	}
	
	private void bootstrapLayout() {
		add(new JLabel("Schedule PaneL"), BorderLayout.CENTER);
		
	}

}
