package projects;

import projects.dao.DbConnection;

public class ProjectsApp {

	public static void main(String[] args) {

		DbConnection.getConnection();
	
	} // end MAIN

} // end CLASS
