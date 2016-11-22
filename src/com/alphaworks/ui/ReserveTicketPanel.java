package com.alphaworks.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.alphaworks.R;
import com.alphaworks.helpers.DateTextField;
import com.alphaworks.helpers.Helper;
import com.alphaworks.helpers.ImageReplacedElementFactory;
import com.alphaworks.helpers.SeatType;
import com.alphaworks.helpers.TicketPrinter;
import com.alphaworks.model.ReservationModel;
import com.alphaworks.model.StationModel;
import com.alphaworks.wrappers.Person;
import com.alphaworks.wrappers.Reservation;
import com.alphaworks.wrappers.Station;
import com.alphaworks.wrappers.Train;
import com.qt.datapicker.DatePicker;

@SuppressWarnings("serial")
public class ReserveTicketPanel extends JPanel implements Runnable {

	private DetailsPanel mDetailsPanel;
	private AddPanel mAddPanel;
	private JScrollPane scrollPane;
	private JTable table;
	private JButton remove, submit;
	private DefaultTableModel mTableModel;
	private JComboBox<SeatType> seatType;
	
	public ReserveTicketPanel() {
		super();
		
		mDetailsPanel = new DetailsPanel();
		mAddPanel = new AddPanel();
		mAddPanel.setActionListener(this);
		mTableModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
			
		};
		mTableModel.setColumnCount(3);
		mTableModel.setColumnIdentifiers(new String[] {"ID", "Name", "Age"});
		table = new JTable(mTableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
						remove.setEnabled(table.getSelectedRow() != -1);
			}
		});
		table.setRowHeight(25);
		remove = new JButton(" Remove");
		remove.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				mTableModel.removeRow(table.getSelectedRow());
			}
		});
		submit = new JButton("Done");
		submit.setIcon(R.loadIcon("done.png"));
		remove.setIcon(R.loadIcon("remove.png"));
		
		Dimension d = new Dimension(400, 250);
		mDetailsPanel.setMinimumSize(d);
		mAddPanel.setMinimumSize(d);
		d = new Dimension(110, 50);
		remove.setMinimumSize(d);
		submit.setMinimumSize(d);
		remove.setEnabled(false);
		submit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(mTableModel.getRowCount() <= 0) {
						throw new Exception("Atleast one traveller required!");
					}
					String dateStr = mDetailsPanel.getDate();
					Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
					Reservation r = new Reservation();
					r.setDate(date);
					r.setFrom(mDetailsPanel.getEnteringStation());
					r.setTo(mDetailsPanel.getExitingStation());
					r.setSeatType((SeatType)seatType.getSelectedItem());
					r.setTrain(mDetailsPanel.getSelectedTrain());
					if(!r.getTrain().getSeatingCapacity().containsKey(r.getSeatType().name())) {
						JOptionPane.showMessageDialog(ReserveTicketPanel.this, "Sorry! " + r.getSeatType().name() + " coaches are not availabe on this train!");
						return;
					}
					for(int i=0; i<mTableModel.getRowCount(); ++i) {
						Person p = new Person((String)mTableModel.getValueAt(i, 0), (String)mTableModel.getValueAt(i, 1), Integer.valueOf((String)mTableModel.getValueAt(i,  2)));
						r.addPerson(p);
					}
					boolean succ = ReservationModel.reserve(r);
					if(succ) {
						mDetailsPanel.reset();
						mAddPanel.reset();
						mTableModel.setRowCount(0);
						mTableModel.fireTableDataChanged();
						JOptionPane.showMessageDialog(ReserveTicketPanel.this, "Reservation Success!");
						int ret = JOptionPane.showConfirmDialog(ReserveTicketPanel.this, "Do you want to print the ticket?");
						if( ret == JOptionPane.OK_OPTION) {
							JFileChooser mChooser = new JFileChooser();
							mChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							mChooser.setMultiSelectionEnabled(false);
							int _r = mChooser.showOpenDialog(ReserveTicketPanel.this);
							if(_r == JFileChooser.APPROVE_OPTION) {
								String docString = TicketPrinter.fromReservation(r).build();
								File dir = mChooser.getSelectedFile();
								if(!dir.isDirectory()) {
									JOptionPane.showMessageDialog(ReserveTicketPanel.this, "Please select a directory.", "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
								OutputStream os = new FileOutputStream(new File(dir, "ticket-" + r.getReservationID() + ".pdf"));
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
								JOptionPane.showMessageDialog(ReserveTicketPanel.this, "Success! Ticket saved to the specified directory!");
							}
						}
					}
					else {
						throw new Exception(ReservationModel.getErrorMessage());
					}
				}
				catch(ParseException ex) {
					JOptionPane.showMessageDialog(ReserveTicketPanel.this, 	
							"Please enter a valid date in dd/mm/yyyy.",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
				catch(Exception mException) {
					JOptionPane.showMessageDialog(ReserveTicketPanel.this,
							mException.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		seatType = new JComboBox<>();
		seatType.addItem(SeatType.SL);
		seatType.addItem(SeatType.AC);
		seatType.addItem(SeatType.CC);
		initLayout();
	}
	
	private void initLayout() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = c.gridy = 0;
		c.insets.left = 50;
		add(mDetailsPanel, c);
		c.insets.left = 50;
		c.gridx++;
		c.insets.right = 50;
		c.anchor = GridBagConstraints.CENTER;
		add(mAddPanel, c);
		scrollPane = new JScrollPane(table);
		scrollPane.setMinimumSize(new Dimension(100, 250));
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets.top = 30;
		add(scrollPane, c);
		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.insets.top= 20;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_START;
		add(remove, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
//		add(seatType);
//		c.gridx++;
//		add(submit, c);
		JPanel endPanel = new JPanel();
		endPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		endPanel.add(seatType);
		endPanel.add(submit);
		add(endPanel, c);

//		
		
		
	}
	
	public DetailsPanel getDetailsPanel() {
		return mDetailsPanel;
	}

	@Override
	public void run() {
		int rowCount = mTableModel.getRowCount();
		for(int i=0; i<rowCount; ++i) {
			if(mTableModel.getValueAt(i, 0).equals(mAddPanel.getID())) {
				JOptionPane.showMessageDialog(this, "Duplicate ID detected!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		mTableModel.addRow(new String[] { mAddPanel.getID(), mAddPanel.getPersonName(),String.valueOf(mAddPanel.getAge())} );
		mTableModel.fireTableDataChanged();
	}
	
	
}


@SuppressWarnings("serial")
class DetailsPanel extends JPanel implements ActionListener {
	
	//private JTextField  date;
	private DateTextField date;
	private JComboBox<Train> trains;
	private JComboBox<Station> from , to;
	private JButton dateTrigger;
	
	private static final int NO_OF_COLUMNS = 20;
	public DetailsPanel() {
		trains = new JComboBox<Train>();
		Helper.populateTrainCombo(trains);
		from = new JComboBox<>();
		to = new JComboBox<>();
		Helper.populateStationsCombo(from);
		Helper.populateStationsCombo(to);
		date = new DateTextField(NO_OF_COLUMNS);
		dateTrigger = new JButton("Choose..");
		dateTrigger.setActionCommand("toggle_date");
		dateTrigger.addActionListener(this);
		init();
		try {
			setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
													"Please enter journey details",
													TitledBorder.LEADING, 
													TitledBorder.DEFAULT_POSITION, 
													Font.createFont(Font.PLAIN, R.loadFile("Ubuntu-R.ttf")).deriveFont(13.0f)
									),
							BorderFactory.createEmptyBorder(30, 30, 0, 30)
						)
					);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
						
	}
	
	
	public void setDetails(Reservation r) {
		if( r != null ) {
			from.setSelectedItem(r.getFrom());
			to.setSelectedItem(r.getTo());
			date.setText(new SimpleDateFormat("dd/MM/yyyy").format(r.getDate()));
		}
	}
	public void reset() {
		trains.setSelectedIndex(0);
		from.setSelectedIndex(0);
		to.setSelectedIndex(0);
		date.setText("");
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("toggle_date")) {
			DatePicker picker = new DatePicker(date);
			try {
				picker.setSelectedDate(new SimpleDateFormat("dd/MM/yyyy").parse(date.getText()));
			} catch (ParseException e1) {
			}
			picker.start(date);
		}
	}



	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = c.gridy = 0;
		c.insets.bottom = 20;
		c.anchor = GridBagConstraints.LINE_END;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Train No. : "), c);
		c.gridx++;
		c.insets.left = 10;
		c.gridwidth = 2;
		add(trains, c);
		c.gridy++;
		c.gridx = 0;
		c.insets.left = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("Date  : "), c);
		c.gridx++;
		c.insets.left = 10;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0f;
		add(date, c);
		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		add(dateTrigger, c);
		c.gridy++;
		c.gridx = 0;
		c.insets.left = 0;
		add(new JLabel("From : "), c);
		c.gridx++;
		c.insets.left =10;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(from, c);
		c.gridy++; 
		c.gridx = 0;
		c.insets.left = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		add(new JLabel("To : "), c);
		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets.left = 10;
		c.gridwidth = 2;
		add(to, c);
		
	}
	
	public Train getSelectedTrain() {
		return (Train)trains.getSelectedItem();
	}
	
	public Station getEnteringStation() {
		return StationModel.getAll().get(from.getSelectedIndex());
	}
	
	public Station getExitingStation() {
		return StationModel.getAll().get(to.getSelectedIndex());
	}
	
	public String getDate() {
		return date.getText();
	}
	


	public void setTrain(Train t) {
		trains.setSelectedItem(t);
	}
	
	public void setEnteringStation(Station station) {
		from.setSelectedItem(station);
	}
	
	public void setExitingStation(Station station) {
		to.setSelectedItem(station);
	}
	
	public void setDate(String date) {
		this.date.setText(date);
	}
	
}


@SuppressWarnings("serial")
class AddPanel extends JPanel implements ActionListener {
	private JTextField name, id;
	private JSpinner age;
	private JButton add, reset;
	private Runnable mRunnable;
	
	private static final int COLUMNS = 20;
	
	public AddPanel() {
		name = new JTextField(COLUMNS);
		id = new JTextField(COLUMNS);
		age = new JSpinner();
		age.setModel(new SpinnerNumberModel(1, 1, 130, 1));
		add = new JButton("Add");
		reset = new JButton("Reset");
		add.setActionCommand("add");
		add.addActionListener(this);
		reset.setActionCommand("reset");
		reset.addActionListener(this);
		init();
		try {
			setBorder(
					BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(
								BorderFactory.createEtchedBorder(), 
								"Please fill in the details", 
								TitledBorder.LEADING, 
								TitledBorder.DEFAULT_POSITION, 
								Font.createFont(Font.PLAIN, R.loadFile("Ubuntu-R.ttf")).deriveFont(13.0f)
							),
						BorderFactory.createEmptyBorder(30, 30, 0, 30)
					)
				);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = c.gridy = 0;
		c.insets.bottom = 20;
		c.insets.left = 10;
		add(new JLabel("ID : "), c);
		c.gridx++;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(id, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Name : "), c);
		c.gridx++;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(name, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(new JLabel("Age : "), c);
		c.gridx++;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(age, c);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy++;
		c.anchor = GridBagConstraints.LINE_START;
		c.gridwidth = 1;
		add(add, c);
		c.gridx++;
		add(reset, c);
	
	}
	
	public void reset() {
		name.setText("");
		id.setText("");
		age.setValue(1);
	}
	

	public String getPersonName() {
		return name.getText();
	}
	
	public Integer  getAge() {
		return (Integer)age.getValue();
	}
	
	public String getID() {
		return id.getText();
	}
	

	public void setActionListener(Runnable l) {
		this.mRunnable = l;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("add")) {
			handleAdd();
		}
		else if(e.getActionCommand().equals("reset")) {
			name.setText("");
			age.setValue(1);
			id.setText("");
		}
	}
	
	private void handleAdd() {
		try {
			if(name.getText().length() < 4)
				throw new Exception("Name should be atleast 4 characters long.");
		
			if(id.getText().length() < 5) {
				throw new Exception("ID should be atleast 5 characters long.");
			}
			
			if(mRunnable != null)
				mRunnable.run();
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(getParent(), e.getMessage());
		}
	}
	
	
	
}

