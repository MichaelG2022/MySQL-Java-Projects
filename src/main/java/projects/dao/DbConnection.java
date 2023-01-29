package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {

	private static final String HOST = "localhost";
	private static final String PASSWORD = "projects";
	private static final int PORT = 3306;
	private static final String SCHEMA = "projects";
	private static final String USER = "projects";

	/*
	 * Generates the url for the database connection, then tries to connect,
	 * throwing execeptions if the connection is not made.
	 */
	public static Connection getConnection() {
		String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false", HOST, PORT, SCHEMA, USER,
				PASSWORD);

		try {
			Connection conn = DriverManager.getConnection(url);
			System.out.println("\nConnection to schema '" + SCHEMA + "' successfully obtained.");
			return conn;
		} catch (SQLException e) {
			System.out.println("Unable to get connection at " + url);
			throw new DbException("Unable to get connection at " + url);
		}

	}

} // end CLASS
