package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.alphaworks.R;
import com.alphaworks.helpers.DateTextField;
import com.alphaworks.model.ReservationModel;
import com.qt.datapicker.DatePicker;

@SuppressWarnings("serial")
public class TicketCancelPanel extends JPanel {
	
	private ContainerPanel mContainerPanel;
	public TicketCancelPanel() {
		super();
		mContainerPanel = new ContainerPanel();
		init();
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets.bottom = 150;
		add(mContainerPanel, c);
	}
	
	class ContainerPanel extends JPanel implements ActionListener {
		private JTextField ticketNo;
		private DateTextField mDateField;
		private JButton cancel, openDateSelect;
		private DatePicker mDatePicker;
		
		public ContainerPanel() {
			super();
			ticketNo = new JTextField(25);
			cancel = new JButton("Cancel");
			mDateField = new DateTextField(25);
			mDatePicker = new DatePicker(mDateField);
			openDateSelect = new JButton("Choose..");
			openDateSelect.addActionListener(this);
			openDateSelect.setActionCommand("datechoose");
			cancel.addActionListener(this);
			cancel.setActionCommand("cancel");
			cancel.setPreferredSize(new Dimension(90, 45));
			cancel.setIcon(R.loadIcon("cancel.png"));
			
			init();
			setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(
									BorderFactory.createEtchedBorder(), 
									"Please fill in your ticket details"
								),
							BorderFactory.createEmptyBorder(30, 50, 30, 50)
						)
					);
		}
		
		private void init() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = c.gridy = 0;
			c.insets.top = 20;
			c.insets.left = 10;
			c.anchor = GridBagConstraints.LINE_END;
			add(new JLabel("Ticket No. : "), c);
			c.gridx++;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(ticketNo, c);
			c.fill = GridBagConstraints.NONE;
			c.gridwidth = 1;
			c.gridy++;
			c.gridx = 0;
			add(new JLabel("Date : "), c);
			c.gridx++;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(mDateField, c);
			c.fill = GridBagConstraints.NONE;
			c.gridx++;
			add(openDateSelect, c);
			c.gridx = 1;
			c.gridy++;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.LINE_START;
			add(cancel, c);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("cancel")) {

				int sure = JOptionPane.showConfirmDialog(TicketCancelPanel.this,"Are you sure?");
				if(sure != JOptionPane.OK_OPTION) {
					return;
				}
				
				int reservationID = -1;
				Date d;
				try {
					reservationID = Integer.parseInt(ticketNo.getText());
					d = new SimpleDateFormat("dd/MM/yyyy").parse(mDateField.getText());
					if(!ReservationModel.cancelReservation(reservationID, d)) {
						showErrorMessage("No reservation found for given information.");
						return;
					}
					else {
						JOptionPane.showMessageDialog(TicketCancelPanel.this, "Ticket cancelled successfully!");
						resetUI();
					}
				}
				catch(NumberFormatException ex) {
					showErrorMessage("Please enter a valid ticket number.");
					return;
				}
				catch(ParseException ex) {
					showErrorMessage("Please enter a valid date.");
					return;
				}
				catch(Exception ex) {
					showErrorMessage(ex.getMessage());
				}
				
			}
			else if(e.getActionCommand().equals("datechoose")) {
				try {
					mDatePicker.setSelectedDate(new SimpleDateFormat("dd/MM/yyyy").parse(mDateField.getText()));
				} catch (ParseException e1) {
				}
				mDatePicker.start(mDateField);
			}
		}
		
		public void showErrorMessage(String msg) {
			JOptionPane.showMessageDialog(TicketCancelPanel.this, msg, "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		private void resetUI() {
			ticketNo.setText("");
			mDateField.setText("");
		}
		
	}

}
