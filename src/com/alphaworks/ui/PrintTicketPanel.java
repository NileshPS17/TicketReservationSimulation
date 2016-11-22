package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.alphaworks.R;
import com.alphaworks.helpers.DateTextField;
import com.alphaworks.helpers.Helper;
import com.alphaworks.helpers.ImageReplacedElementFactory;
import com.alphaworks.helpers.TicketPrinter;
import com.alphaworks.model.ReservationModel;
import com.alphaworks.wrappers.Reservation;
import com.qt.datapicker.DatePicker;

@SuppressWarnings("serial")
public class PrintTicketPanel extends JPanel {

	private ContainerPanel mContainerPanel;
	
	public PrintTicketPanel() {
		super();
		mContainerPanel = new ContainerPanel();
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets.bottom = 150;
		add(mContainerPanel, c);
	}

	class ContainerPanel extends JPanel implements ActionListener {
		private JTextField ticketNo;
		private DateTextField mDateField;
		private JButton ok, openDateSelect;
		private JFileChooser mFileChooser;
		private DatePicker mDatePicker;
		
		public ContainerPanel() {
			super();
			ticketNo = new JTextField(25);
			ok = new JButton("Print");
			ok.setActionCommand("filechoose");
			mDateField = new DateTextField(25);
			mDatePicker = new DatePicker(mDateField);
			mFileChooser = new JFileChooser();
			mFileChooser.setMultiSelectionEnabled(false);
			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			mFileChooser.setDialogTitle("Select destination directory");
			openDateSelect = new JButton("Choose..");
			openDateSelect.addActionListener(this);
			openDateSelect.setActionCommand("datechoose");
			ok.addActionListener(this);
			ok.setPreferredSize(new Dimension(90, 45));
			ok.setIcon(R.loadIcon("print.png"));
			
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
			add(ok, c);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("filechoose")) {
				try {
					if(!Helper.isValidTicketNo(ticketNo.getText())) {
						throw new Exception("Please enter a valid ticket number.");
					}
					if(Helper.isEmpty(mDateField.getText())) {
						throw new Exception("Please provide the date of your journey.");
					}
		
					int id = Integer.parseInt(ticketNo.getText());
					Date d = new SimpleDateFormat("dd/MM/yyyy").parse(mDateField.getText());
					Reservation mRes = ReservationModel.getReservation(id, d);
					if(mRes == null) {
						JOptionPane.showMessageDialog(PrintTicketPanel.this, "Sorry! No reservation found matching your data.", "Error", JOptionPane.WARNING_MESSAGE);
					}
					else {
						String docString = TicketPrinter.fromReservation(mRes).build();
						int retVal = mFileChooser.showOpenDialog(PrintTicketPanel.this);
						if(retVal == JFileChooser.APPROVE_OPTION) {
							File dir = mFileChooser.getSelectedFile();
							if(!dir.isDirectory()) {
								JOptionPane.showMessageDialog(PrintTicketPanel.this, "Please select a directory.", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							OutputStream os = new FileOutputStream(new File(dir, "ticket-" + id + ".pdf"));
							ITextRenderer renderer = new ITextRenderer();
							renderer.getSharedContext().setReplacedElementFactory(
									new ImageReplacedElementFactory(
											renderer.getSharedContext().getReplacedElementFactory()
											)
									);
							renderer.setDocumentFromString(docString);
							renderer.layout();
							renderer.createPDF(os);
							os.close();
							JOptionPane.showMessageDialog(PrintTicketPanel.this, "Success! Ticket saved to the specified directory!");
						}
			
					}
					
				}
				catch(ParseException pex) {
					JOptionPane.showMessageDialog(PrintTicketPanel.this, "Please enter a valid date.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				catch(Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(PrintTicketPanel.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
				//int retVal = mFileChooser.showOpenDialog(PrintTicketPanel.this);
			}
			else if(e.getActionCommand().equals("datechoose")) {
				try {
					mDatePicker.setSelectedDate(new SimpleDateFormat("dd/MM/yyyy").parse(mDateField.getText()));
				} catch (ParseException e1) {
				}
				mDatePicker.start(mDateField);
			}
		}
		
		
	}

}
