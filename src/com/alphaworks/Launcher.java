package com.alphaworks;
import java.awt.EventQueue;
import java.sql.SQLException;

import javax.swing.UIManager;

import com.alphaworks.model.Database;
import com.alphaworks.ui.MainWindow;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;

public class Launcher {
	/**
	 * Up up and away .. !
	 * @param args
	 * @throws Exception
	 */
	public static void main(String... strings) throws Exception {
	
		Class.forName("com.mysql.jdbc.Driver");
		//UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {
			// alrighty
		}
		
		try {
			CLIArguments args = CliFactory.parseArguments(CLIArguments.class, strings);
			Database.setHost(args.getHost());
			Database.setName(args.getDatabaseName());
			Database.setPwd(args.getPassword());
			Database.setUser(args.getUser());
			Database.invalidate();
		}
		catch(ArgumentValidationException e) {
			e.printStackTrace();
		}
		catch(SQLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MainWindow().setVisible(true);
			}
		});
	
	}
}


interface CLIArguments {
	
	@Option(defaultValue = "127.0.0.1", shortName = "h", longName = "host")
	String getHost();
	
	@Option(defaultValue = "railway", shortName = "db", longName = "database")
	String getDatabaseName();
	
	@Option(defaultValue = "mysqladmin", shortName = "p", longName = "password")
	String getPassword();
	
	@Option(defaultValue = "root", shortName = "u", longName = "user")
	String getUser();
	
}
	