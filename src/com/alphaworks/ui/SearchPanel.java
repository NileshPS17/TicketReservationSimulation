package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.alphaworks.helpers.DateTextField;
import com.alphaworks.helpers.Helper;
import com.alphaworks.model.TrainModel;
import com.alphaworks.model.TrainModel.SearchResult;
import com.alphaworks.wrappers.Reservation;
import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;
import com.qt.datapicker.DatePicker;

@SuppressWarnings("serial")
public class SearchPanel extends JPanel implements ActionListener {
	
	private DateTextField mDateTextField;
	private JComboBox<Station> fromStation, toStation;
	private JButton reserveTicket, search;
	private JTable table;
	private DefaultTableModel mDefaultModel = null;
	
	private static final int NO_OF_COLUMNS = 20;
	private static final String columnNames[] = {
			"Train No.",
			"Name",
			"Source Dep. time",
			"Dest Arr. time",
			"Distance"
	};
	
	public SearchPanel() {
		super();
		Dimension d = new Dimension(177, 27);
		fromStation = new JComboBox<>();
		fromStation.setMinimumSize(d);
		toStation = new JComboBox<>();
		Helper.populateStationsCombo(fromStation);
		Helper.populateStationsCombo(toStation);
		toStation.setMinimumSize(d);
		mDateTextField = new DateTextField(NO_OF_COLUMNS);
		mDateTextField.setMinimumSize(d);
		mDateTextField.setEditable(false);
		reserveTicket = new JButton("Reserve Ticket");
		search = new JButton("Get Trains");
		search.setActionCommand("search");
		search.addActionListener(this);
		d = new Dimension(160, 70);
		reserveTicket.setMinimumSize(d);
		reserveTicket.setPreferredSize(d);
		reserveTicket.setActionCommand("reserve");
		reserveTicket.addActionListener(this);
		search.setMinimumSize(d);
		search.setPreferredSize(d);
		mDefaultModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table = new JTable(mDefaultModel);
		table.setRowHeight(25);
		initLayout();
	}
	
	private NavigationListener mListener;
	
	public void setNavigationListener(NavigationListener arg) {
		mListener = arg;
	}
	
	
	private void initLayout() {

		JButton btn = new JButton("Choose..");
		btn.setActionCommand("toggle_date");
		btn.addActionListener(this);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		add(new JLabel("From : "), c);
		c.gridx++;
		c.insets.left = 0;
		add(fromStation, c);
		c.gridx++;
		c.insets.left = 20;
		add(new JLabel("To : "), c);
		c.gridx++;
		c.insets.left = 0;
		add(toStation, c);
		c.gridx++;
		c.insets.left =  20;
		c.gridx ++;
		c.insets.left = 20;
		add(new JLabel("Date : "), c);
		c.gridx++;
		c.insets.left = 0;
		add(mDateTextField, c);
		c.gridx++;
		add(btn, c);
		
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		scrollPane.setMinimumSize(new Dimension(500, 200));
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 11;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.top = 20;
		add(scrollPane, c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 5;
		c.insets.left = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.NONE;
		add(reserveTicket, c);
		c.gridx = 5;
		c.anchor = GridBagConstraints.LINE_START;
		add(search, c);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if(action.equals("toggle_date")) {
			DatePicker picker = new DatePicker(mDateTextField);
			try {
				picker.setSelectedDate(new SimpleDateFormat("dd/MM/yyyy").parse(mDateTextField.getText()));
			} catch (ParseException e1) {
			}
			picker.start(mDateTextField);
		}
		else if(action.equals("search")) {
			try{
				validateForm();
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Station from =(Station) fromStation.getSelectedItem(),
					to   = (Station) toStation.getSelectedItem();
			
			Date d;
			try {
				d = new SimpleDateFormat("dd/MM/yyyy").parse(mDateTextField.getText());
			} catch (ParseException e1) {
				e1.printStackTrace();
				return;
			}
			List<SearchResult> list = TrainModel.getTrainsBetweenStations(from.getID(), to.getID(), d);
			if(list == null || list.size() == 0) {
				JOptionPane.showMessageDialog(this, "Sorry! No trains found!");
				mDefaultModel.setRowCount(0);
				mDefaultModel.fireTableDataChanged();
				
			}
			else {
				mDefaultModel.setRowCount(0);
				mDefaultModel.fireTableDataChanged();
				for(SearchResult sr : list) {
					mDefaultModel.addRow(
							new String[] {
									String.valueOf(sr.trainNo),
									sr.trainName,
									sr.depTime,
									sr.arrTime,
									String.valueOf(sr.distance)
							}
						);
				}
				mDefaultModel.fireTableDataChanged();
			}
				
		}
		else if(action.equals("reserve")) { 
			int selectedRow = table.getSelectedRow();
			if(selectedRow == -1) {
				JOptionPane.showMessageDialog(this, "Please select a train first.");
				return;
			}
			Train selectedTrain = TrainModel.getTrainByID(Integer.valueOf((String)mDefaultModel.getValueAt(selectedRow, 0)));
			Reservation r = new Reservation();
			r.setTrain(selectedTrain);
			try {
				r.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(mDateTextField.getText()));
			}
			catch(ParseException ex) {
				ex.printStackTrace();
			}
			r.setFrom((Station)fromStation.getSelectedItem());
			r.setTo((Station)toStation.getSelectedItem());
			setSearchInfo(r);
			if(mListener != null) {
				mListener.fireEvent(HomePanel.EVENT_RESERVE, r);
			}
		}
	}
	
	private Reservation reservationDetailsCache = null;
	
	public Reservation getSearchInfo() {
		Reservation r = reservationDetailsCache;
		reservationDetailsCache = null;
		return r;
	}
	
	private void setSearchInfo(Reservation r) {
		this.reservationDetailsCache = r;
	}
	
	private void validateForm() {
		if(fromStation.getSelectedIndex() == -1 || toStation.getSelectedIndex() == -1) {
			throw new RuntimeException("Please select both stations!");
		}
		else if(fromStation.getSelectedIndex() == toStation.getSelectedIndex()) {
			throw new RuntimeException("Source and destination cannot be same!");
		}
		else if(Helper.isEmpty(mDateTextField.getText())) {
			throw new RuntimeException("Please enter a date.");
		}
	
	}
	
}


