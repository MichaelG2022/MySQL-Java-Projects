package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {
	
	// Instantiates a ProjectDao object.
	private ProjectDao projectDao = new ProjectDao();

	/*
	 * Calls the insertProject from the ProjectDao class with the just entered project as an argument. Returns the project object to
	 * the createProject method in the ProjectApp class if the project is successfully added to the database.
	 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	} // end addProject

} // end CLASS
