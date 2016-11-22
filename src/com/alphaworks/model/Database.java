package com.alphaworks.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a gateway to the database.
 * @author nilesh
 *
 */
public class Database {
	private static String HOST = "127.0.0.1";
	//private static final String DB = "jdbc:mysql://127.0.0.1/railway?useSSL=false";
	private static String DB_NAME = "railway"; //aka localhost
	private static String USER = "root";
	private static String PWD = "mysqladmin";
	private static Connection connInstance = null;
	
	private Database() {
		//pass
	}
	
	public static String getHost() {
		return HOST;
	}


	public static void setHost(String hOST) {
		HOST = hOST;
	}


	public static String getName() {
		return DB_NAME;
	}


	public static void setName(String dB_NAME) {
		DB_NAME = dB_NAME;
	}


	public static String getUser() {
		return USER;
	}
	
	public static void setUser(String user) {
		USER = user;
	}


	public static String getPwd() {
		return PWD;
	}
	
	public static void setPwd(String pwd) {
		PWD = pwd;
	}
	
	
	public static void invalidate() throws SQLException {
			if(connInstance != null && connInstance.isClosed()) {
				connInstance.close();
			}
			
			connInstance = DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DB_NAME + "?useSSL=false", USER, PWD);
	}


	/**
	 * Get the singleton database connection instance. An {@link java.lang.Exception Exception } is thrown
	 * if the connection could not be made or is not in valid state.
	 * @return {@link java.sql.Connection Connetion}
	 */
	public static Connection getInstance() {
		try {
			if( connInstance == null || connInstance.isClosed()) {
				connInstance = DriverManager.getConnection("jdbc:mysql://" + HOST + "/" + DB_NAME + "?useSSL=false", USER, PWD);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to establish database connection.");
		}
		return connInstance;
	}
	
	
}
