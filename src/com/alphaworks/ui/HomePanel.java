package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomePanel extends JPanel implements ActionListener {

	public static final String EVENT_SEARCH = "search";
	public static final String EVENT_RESERVE = "reserve";
	public static final String EVENT_PRINT  = "print";
	public static final String EVENT_SCHEDULE = "schedule";
	public static final String EVENT_CANCEL = "cancel";
	public static final String EVENT_STATUS = "status";
	
	
	private OnMenuClickedListener mOnMenuClickedListener = null;
	private JButton btnSearch, btnPrint, btnReserve, btnCancel, btnStatus, btnSchedule;
	private BufferedImage mImage;

	public HomePanel() {
		btnSearch = new JButton("Search", new ImageIcon("res/search.png"));
		btnPrint = new JButton("Print Ticket", new ImageIcon("res/print.png"));
		btnReserve = new JButton("Reserve Ticket", new ImageIcon("res/reserve.png"));
		btnCancel = new JButton("Cancel", new ImageIcon("res/cancel.png"));
		btnStatus = new JButton("Status", new ImageIcon("res/status.png"));
		btnSchedule = new JButton("Schedule", new ImageIcon("res/time.png"));

		init();
	}

	/**
	 * Bootstrap the panel. Add any action listeners and arrange the components.
	 * 
	 * @return void
	 */
	private void init() {
		// setLayout(new GridBagLayout());
		Dimension d = new Dimension(160, 70);
		btnSearch.setActionCommand(EVENT_SEARCH);
		btnSearch.setPreferredSize(d);
		btnSearch.addActionListener(this);
		btnPrint.setPreferredSize(d);
		btnPrint.setActionCommand(EVENT_PRINT);
		btnSearch.setPreferredSize(d);
		btnPrint.addActionListener(this);
		btnReserve.setActionCommand(EVENT_RESERVE);
		btnReserve.setPreferredSize(d);
		btnReserve.addActionListener(this);
		btnCancel.setActionCommand(EVENT_CANCEL);
		btnCancel.setPreferredSize(d);
		btnCancel.addActionListener(this);
		btnStatus.setActionCommand(EVENT_STATUS);
		btnStatus.setPreferredSize(d);
		btnStatus.addActionListener(this);
		btnSchedule.setActionCommand(EVENT_SCHEDULE);
		btnSchedule.setPreferredSize(d);
		btnSchedule.addActionListener(this);

		try {
			mImage = ImageIO.read(new File("res/railway.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		c.weighty = .3;
		add(btnSearch, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = .3;
		c.anchor = GridBagConstraints.CENTER;
		add(btnReserve, c);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = .3;
		c.anchor = GridBagConstraints.CENTER;
		add(btnPrint, c);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 3;
		c.anchor = GridBagConstraints.CENTER;
		c.ipadx = c.ipady = 100;
		add(new JLabel(new ImageIcon(mImage)), c);

		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		add(btnStatus, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(btnSchedule, c);
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.CENTER;
		add(btnCancel, c);
		
		setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

	}

	/**
	 * Set a callback to be invoked when one of the menu items is clicked.
	 * 
	 * @param l
	 *            An instance of OnMenuClickedListener
	 */
	public void setMenuClickListener(OnMenuClickedListener l) {
		this.mOnMenuClickedListener = l;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (mOnMenuClickedListener != null) {
			mOnMenuClickedListener.onMenuClicked(e.getActionCommand());
		}
	}

	/**
	 * Used for specifying a callback to be invoked when user performs an
	 * action.
	 * 
	 * @author nilesh
	 *
	 */
	public static interface OnMenuClickedListener {
		public void onMenuClicked(String action);
	}

}
