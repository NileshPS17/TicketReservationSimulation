package com.alphaworks.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.alphaworks.R;
import com.alphaworks.wrappers.Reservation;

/**
 * The main frame of the application.
 * @author nilesh
 *
 */

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener, 
								HomePanel.OnMenuClickedListener, NavigationListener
									{

	private static final String HOME = "home_card";
	private static final String windowName = "Online Ticket Reservation";

	private Box backBox;
	private JButton back;
	private JPanel cardPanel;
	private JLabel titleLabel;
	private CardLayout cardLayout;
	private SearchPanel mSearchPanel;
	private StatusPanel mStatusPanel;
	private PrintTicketPanel mPrintPanel;
	private SchedulePanel mSchedulePanel;
	private TicketCancelPanel mCancelPanel;
	private ReserveTicketPanel mReservePanel;
	
	
	private final  HashMap<String, String> titleMap;
	
	public MainWindow() {
		super(windowName);
		setMinimumSize(new Dimension(400, 400));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(900, 400));
		setLocationRelativeTo(null);
		//add a back menu
		backBox = Box.createHorizontalBox();
		back = new JButton("", R.loadIcon("back.png"));
		back.setOpaque(false);
		//back.setFocusPainted(false);
		back.setContentAreaFilled(false);
		back.addActionListener(this);
		back.setActionCommand("back");
		backBox.add(back);
		backBox.add(Box.createHorizontalGlue());
		titleLabel = new JLabel("ion", JLabel.CENTER);
		try {
			Font f = Font.createFont(Font.PLAIN, R.loadFile("Ubuntu-R.ttf"));
			f = f.deriveFont(22.0f);
			titleLabel.setFont(f);
		} catch (Exception e) {
			//Leave it :-(
		}
		titleLabel.setForeground(Color.DARK_GRAY);
		titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 70));
		backBox.add(titleLabel);
		backBox.add(Box.createHorizontalGlue());
		add(backBox, BorderLayout.NORTH);
		backBox.setVisible(false);
		//bootstrap title map
		titleMap = new HashMap<>();
		titleMap.put(HomePanel.EVENT_CANCEL, "Cancel Ticket");
		titleMap.put(HomePanel.EVENT_PRINT, "Print Ticket");
		titleMap.put(HomePanel.EVENT_RESERVE,"Reserve Ticket");
		titleMap.put(HomePanel.EVENT_SCHEDULE,"Train Schedule");
		titleMap.put(HomePanel.EVENT_SEARCH, "Trains b/w Stations");
		titleMap.put(HomePanel.EVENT_STATUS, "Live Train Status");
		
		mSearchPanel = new SearchPanel();
		mPrintPanel = new PrintTicketPanel();
		mSchedulePanel = new SchedulePanel();
		mCancelPanel = new TicketCancelPanel();
		mReservePanel = new ReserveTicketPanel();
		mStatusPanel = new StatusPanel();
		
		mSearchPanel.setNavigationListener(this);
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
		cardPanel.add(mSearchPanel, HomePanel.EVENT_SEARCH);
		cardPanel.add(mPrintPanel, HomePanel.EVENT_PRINT);
		cardPanel.add(mSchedulePanel, HomePanel.EVENT_SCHEDULE);
		cardPanel.add(mCancelPanel, HomePanel.EVENT_CANCEL);
		cardPanel.add(mReservePanel, HomePanel.EVENT_RESERVE);
		cardPanel.add(mStatusPanel, HomePanel.EVENT_STATUS);
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
		file.addSeparator();
		file.add(exit);
		aboutUs.setActionCommand("about");
		aboutUs.addActionListener(this);
		contact.setActionCommand("contact");
		contact.addActionListener(this);
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
		else if(action.equals("contact")) {
			JOptionPane.showMessageDialog(this, "For any further assistance, contact sampleemail@foodomain.com");
		}
		else if(action.equals("about")) {
			JOptionPane.showMessageDialog(this, "Created By Team!");
		}
	
	}

	/**
	 * Bring another card to the top of the deck.
	 * @param action A String to identify the new card.
	 * @return Boolean - true if there was any changes, false otherwise.
	 */

	private boolean showCard(String action) {
		if( action.equals(HomePanel.EVENT_SEARCH)   || 
			action.equals(HomePanel.EVENT_PRINT) 	|| 
			action.equals(HomePanel.EVENT_RESERVE) 	||
			action.equals(HomePanel.EVENT_SCHEDULE) ||
			action.equals(HomePanel.EVENT_CANCEL) 	||
			action.equals(HomePanel.EVENT_STATUS)) {
			
			cardLayout.show(cardPanel, action);
			titleLabel.setText(titleMap.get(action));
			backBox.setVisible(true);
			return true;
		}
		
		
		return false;
		
	}
	
	@Override
	public void onMenuClicked(String action) {
		showCard(action);
	}

	@Override
	public void fireEvent(String action, Object arg) {
		back.doClick();
		if(showCard(action)) {
			if(action.equals(HomePanel.EVENT_RESERVE)) {
				if(! (arg instanceof Reservation))
					throw new IllegalArgumentException("Please provide a valid train no.");
				
				mReservePanel.getDetailsPanel().setDetails((Reservation)arg);
			}
		}
	}
	

	
	

}


