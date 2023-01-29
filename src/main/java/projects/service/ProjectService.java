package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;

public class ProjectService {

	// Instantiates a ProjectDao object.
	private ProjectDao projectDao = new ProjectDao();

	/*
	 * Calls the insertProject from the ProjectDao class with the just-entered
	 * project object as an argument.
	 * 
	 * Returns the project object to the createProject method in the ProjectApp
	 * class if the project is successfully added to the database.
	 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	} // end addProject

	/*
	 * Calls the getAllProjectNames method in the ProjectDao class.
	 * 
	 * Returns the List of Projects to the getProjectNames method in the
	 * ProjectsApps class.
	 */
	public List<Project> getListOfProjectNames() {
		return projectDao.getAllProjectNames();
	} // end getListOfProjectNames

	/*
	 * Calls the fetchProjectByIdDao in the ProjectDao class, passing the selected
	 * project id.
	 * 
	 * Uses empty Lambda expression to throw a NoSuchElementException if the project
	 * does not exist in the data base.
	 */
	public Project fetchProjectByIdService(Integer projectId) {
		return projectDao.fetchProjectByIdDao(projectId)
				.orElseThrow(() -> new NoSuchElementException("Project with ID=" + projectId + " does not exist"));
	} // end fetchProjectByIdService

} // end CLASS
