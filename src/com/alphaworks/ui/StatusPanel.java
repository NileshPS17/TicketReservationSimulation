package com.alphaworks.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.alphaworks.helpers.Helper;
import com.alphaworks.model.TrainModel;
import com.alphaworks.model.TrainModel.Status;
import com.alphaworks.wrappers.Train;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel implements ActionListener {
	private JComboBox<Train> trainNo;
	private JButton find;
	private JLabel info;
	
	public StatusPanel() {
		super();
		trainNo =new JComboBox<>();
		Helper.populateTrainCombo(trainNo);
		find = new JButton("Go!");
		find.addActionListener(this);
		info = new JLabel();
		info.setVerticalAlignment(SwingConstants.CENTER);
		info.setVisible(true);
		info.setText("Lorem ipsum dolor al as amet");
		info.setForeground(Color.DARK_GRAY);
		try {
			Font f = Font.createFont(Font.PLAIN, new File("res/Ubuntu-R.ttf"));
			f = f.deriveFont(16.0f);
			info.setFont(f);
		}
		catch(Exception e) {
			//pass
		}
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets.top = 50;
		add(new JLabel("Train No. : "), c);
		c.gridx++;
		add(trainNo, c);
		c.gridx++;
		c.insets.right=40;
		add(find, c);
		c.weighty = 1.0f;
		c.gridx = 0;
		c.gridy++;
		add(new JLabel(""), c);
//		c.insets.right = 0;
//		c.weighty=1.0;
//		c.gridx = 0;
//		c.gridy++;
//		c.gridwidth = 3;
//		c.insets.top = 0;
//		add(info, c);
//		c.gridy++;
//		c.weighty = 1.0;
//		c.gridx = 0;
//		c.insets.top = 0;
//		add(new JLabel(""), c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		info.setVisible(true);
		TrainModel.Status st = TrainModel.getStatus((Train)trainNo.getSelectedItem());
		String msg = "";
		switch(st.code) {
		case Status.FINISHED:
			msg = "Your train has reached the destination " + st.lastStation.toString() + " at " + st.atTime + "!";
			break;
		case Status.LEFT_STATION:
			msg = "Your train has just left " + st.lastStation.toString() + " at " + st.atTime + " !";
			break;
		case Status.NOT_RUNNING:
			msg = "Sorry! This train is not running today!";
			break;
		case Status.NOT_STARTED:
			msg = "Your train has not left " + st.lastStation.toString() + ". It\'ll do so by " + st.atTime+ ".";
			break;
		}
		
		JOptionPane.showMessageDialog(StatusPanel.this, msg);
	}
	
	
}
