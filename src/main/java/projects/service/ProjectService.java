package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

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
	 * Returns the Stream-sorted-by-projectID List of Projects to the
	 * getProjectNames method in the ProjectsApps class.
	 */
	public List<Project> getListOfProjectNames() {
		// @formatter:off
		return projectDao.getAllProjectNames()
					.stream()
					.sorted((p1, p2) -> p1.getProjectId() - p2.getProjectId())
					.collect(Collectors.toList());		
				// @formatter:on
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

	/*
	 * Calls fetchProjectByIdDao and passes the project ID of the project to be
	 * updated.
	 * 
	 * Tests boolean value returned from modifyProjectDetailsDao for True or False.
	 * 
	 * True means the project details were updated successfully, and false means the
	 * project ID does not exist and an exception is thrown.
	 * 
	 */
	public void modifyProjectDetailsService(Project project) {
		if (!projectDao.modifyProjectDetailsDao(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}
	} // end modifyProjectDetails

	/*
	 * Calls deleteProjectDao and passes the project ID of the project to be
	 * updated.
	 */
	public void deleteProjectService(Integer projectId) {
		if (!projectDao.deleteProjectDao(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
	} // end deleteProjectService

} // end CLASS
