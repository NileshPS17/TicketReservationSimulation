package com.alphaworks.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MainWindow extends JFrame implements ActionListener, HomePanel.OnMenuClickedListener {

	private static final String HOME = "home_card";
	
	private static final String windowName = "Online Ticket Reservation";
	private Box backBox;
	private JPanel cardPanel;
	private CardLayout cardLayout;
	
	public MainWindow() {
		super(windowName);
		setMinimumSize(new Dimension(400, 400));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(900, 400));
		setLocationRelativeTo(null);
		
		//add a back menu
		backBox = Box.createHorizontalBox();
		final JButton back = new JButton("Back", new ImageIcon("res/back.png"));
		back.setOpaque(false);
		//back.setFocusPainted(false);
		back.setContentAreaFilled(false);
		back.addActionListener(this);
		back.setActionCommand("back");
		backBox.add(back);
		backBox.add(Box.createHorizontalGlue());
		add(backBox, BorderLayout.NORTH);
		backBox.setVisible(false);
		addMenuBar();
		bootstrapCardLayout();
		pack();
	}
	
	/**
	 * Bootstrap card layout.
	 */
	private void bootstrapCardLayout() {
		HomePanel mHomePanel = new HomePanel();
		mHomePanel.setMenuClickListener(this);
		cardLayout = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(cardLayout);
		cardPanel.add(mHomePanel, HOME);
		cardPanel.add(new SearchPanel(), HomePanel.EVENT_SEARCH);
		cardPanel.add(new PrintTicketPanel(), HomePanel.EVENT_PRINT);
		cardPanel.add(new SchedulePanel(), HomePanel.EVENT_SCHEDULE);
		cardPanel.add(new TicketCancelPanel(), HomePanel.EVENT_CANCEL);
		cardPanel.add(new ReserveTicketPanel(), HomePanel.EVENT_RESERVE);
		cardPanel.add(new StatusPanel(), HomePanel.EVENT_STATUS);
		getContentPane().add(cardPanel, BorderLayout.CENTER);
	
	}

	/**
	 * Add menubar to the window frame.
	 * @return void
	 */
	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenu about = new JMenu("About");
		JMenuItem exit = new JMenuItem("Exit"), aboutUs = new JMenuItem("About Us"), contact = new JMenuItem("Contact");
		file.add(exit);
		about.add(contact);
		about.addSeparator();
		about.add(aboutUs);
		menuBar.add(file);
		menuBar.add(about);
		setJMenuBar(menuBar);
		exit.setActionCommand("exit");
		exit.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("exit")) {
			setVisible(false);
			System.exit(0);
		}
		else if(action.equals("back")) {
			cardLayout.show(cardPanel, HOME);
			backBox.setVisible(false);
		}
	}


	@Override
	public void onMenuClicked(String action) {
		boolean flag = true;
		if(action.equals(HomePanel.EVENT_SEARCH) 		|| 
				action.equals(HomePanel.EVENT_PRINT) 	|| 
				action.equals(HomePanel.EVENT_RESERVE) 	||
				action.equals(HomePanel.EVENT_SCHEDULE) ||
				action.equals(HomePanel.EVENT_CANCEL) 	||
				action.equals(HomePanel.EVENT_STATUS)) {
			cardLayout.show(cardPanel, action);
		}
		if(flag)
			backBox.setVisible(true);
		
	}
	


}


