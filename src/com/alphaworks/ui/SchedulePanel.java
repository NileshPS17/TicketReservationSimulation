package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.alphaworks.model.TrainModel;
import com.alphaworks.model.TrainModel.ScheduleStop;
import com.alphaworks.wrappers.Train;

@SuppressWarnings("serial")
public class SchedulePanel extends JPanel implements ActionListener {
	
	private JButton btnSubmit;
	private JComboBox<Train> comboBox;
	private JTable table;
	private DefaultTableModel mTableModel;
	private Integer curTrainNo = 0;
	private static String columnHeaders[] = {
			"Train No.", 
			"Station",
			"Arrival Time",
			"Departure Time",
			"Distance (KM)"
	};
	
	public SchedulePanel() {
		comboBox = new JComboBox<Train>();
		for(Train t : TrainModel.getAll()) {
			comboBox.addItem(t);
		}
		btnSubmit = new JButton("Go");
		btnSubmit.setActionCommand("go");
		btnSubmit.addActionListener(this);
		mTableModel = new DefaultTableModel();
		mTableModel.setColumnIdentifiers(columnHeaders);
		table = new JTable(mTableModel);
		table.setRowHeight(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		bootstrapLayout();
	}
	
	private void bootstrapLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets.top = 40;
		c.weightx = 0.86;
		c.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Select train : "), c);
		c.gridx++;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0;
		comboBox.setPreferredSize(new Dimension(250, 30));
		add(comboBox, c);
		c.gridx++;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		add(btnSubmit, c);
//		c.gridy++;
//		c.gridx = 0;
//		c.weighty = 1.0;
//		c.insets.top = 0;
//		add(new JLabel(""), c);
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 0;
		JScrollPane scp = new JScrollPane(table);
		scp.setPreferredSize(new Dimension(900, 500));
		c.insets.top = 20;
		c.insets.left = c.insets.right = 10;
		add(scp ,c);
		c.gridy++;
		c.gridx = 0;
		c.weighty = 1.0;
		add(new JLabel(""), c);
	}
	
	public void setTrainNo(int no) {
		this.curTrainNo = no;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equals("go")) {
			mTableModel.setRowCount(0);
			Train t = (Train)comboBox.getSelectedItem();
			List<ScheduleStop> list = TrainModel.getSchedule(t.getTrainNo());
			int distanceSoFar = 0;
			for(ScheduleStop ss: list) {
				distanceSoFar += ss.distance;
				mTableModel.addRow(
						new String[] {
								String.valueOf(t.getTrainNo()), 
								ss.stationName, 
								ss.arrivalTime, 
								ss.departureTime, 
								String.valueOf(distanceSoFar)
							}
						);
			}
		}
	}

	
	

}
