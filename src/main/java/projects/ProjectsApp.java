package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// Instantiates a Scanner object for capturing user input using System.in
	private Scanner scanner = new Scanner(System.in);

	// Instantiates a projectService object.
	private ProjectService projectService = new ProjectService();

	// Instantiates a Project object to hold the current project.
	private Project currentProject;

	// Creates a List of possible menu choices for the user.
	// @formatter:off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List the available projects",
		"3) Select a project",
		"4) Update project details",
		"5) Delete a project",
		"99) Display the menu"
	); // end operations
	// @formatter:on

	// Calls the processUerSelection method.
	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();
	} // end MAIN

	/*
	 * Uses a while loop to keep the program running until the user quits the
	 * program by hitting the Enter key on a blank line, which calls the exitMenu
	 * method to exit the program.
	 * 
	 * Calls the getUserSelection method to get the user input and uses a switch to
	 * determine what the user wants to do next.
	 * 
	 * An integer input that is not included in the menu causes the Switch default
	 * option to tell the user the selection is not valid and prompts them to try
	 * again.
	 * 
	 * A non-integer input throws an exception and prompts the user to try again.
	 * 
	 * Calls the appropriate method depending on the user input choice.
	 */
	private void processUserSelection() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();
				switch (selection) {

				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;

				case 2:
					getProjectNames();
					break;

				case 3:
					getProjectByIdMain();
					break;

				case 4:
					updateProjectDetails();
					break;

				case 5:
					deleteProject();
					break;

				// breaks out of the processUserSelection while loop to redisplay the menu at
				// user's request
				case 99:
					// Clears currentProject to prevent listing of project again until user chooses
					// to do so.
					currentProject = null;
					break;

				default:
					System.out.println("\n" + selection + " is not a valid choice. Try again.");
				} // end SWITCH
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		} // end WHILE
	} // end processUserSelection

	/*
	 * Calls the printOperations method to display the user selection menu.
	 * 
	 * Calls the getIntInput method and passes the menu user prompt.
	 * 
	 * Uses a ternary conditional operator to process the user menu selection
	 * returned from getIntInput.
	 * 
	 * If the user input is null, a -1 is returned to the processUserSelection
	 * method, which causes the program to exit, otherwise the user input is
	 * returned to the processUserSelection method.
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput(
				"\nEnter a menu choice or press the Enter key to quit. Enter 99 to display menu choices again");

		return Objects.isNull(input) ? -1 : input;
	} // end getUserSelection

	/*
	 * Displays the menu header then the menu choices by using a Lambda expression
	 * on the operations List<> created above.
	 */
	private void printOperations() {
		System.out.println("\nMenu choices:");

		operations.forEach(line -> System.out.println("  " + line));

		if (Objects.isNull(currentProject)) {
			System.out.println("\nYou do not have an active project.");
		} else {
			System.out.println("\n You are viewing: " + currentProject);
		}
	} // end printOperations

	/*
	 * Calls the getStringInput method with the user prompt to capture the user
	 * input selection then attempts to validate the returned input value as an
	 * Integer.
	 *
	 * If the user hits the Enter key on a blank line to quit, a null is returned to
	 * the calling method.
	 * 
	 * If the input is not an Integer, an exception is thrown and the user is
	 * prompted to try again.
	 * 
	 * If the input is a valid integer, it is returned to the calling method.
	 */
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	} // end getIntInput

	/*
	 * Displays the user prompt and uses Scanner to capture the user input line.
	 * 
	 * Uses a ternary conditional operator to return null if the input is blank, or
	 * the trimmed user input to the calling method.
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	} // end getStringInput

	/*
	 * Calls the getStringInput method with the user prompt to capture the user
	 * input selection then attempts to validate the returned input value as a
	 * decimal.
	 *
	 * If the user hits the Enter key on a blank line to quit, a null is returned to
	 * the calling method.
	 * 
	 * If the input is not a decimal, an exception is thrown and the user is
	 * prompted to try again.
	 * 
	 * If the input is a valid decimal, it is returned to the calling method.
	 */
	private BigDecimal getDecimalInput(String string) {
		String input = getStringInput(string);

		if (Objects.isNull(input)) {
			return null;
		}
		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	} // end getDecimalInput

	/*
	 * Captures the user input for each parameter in a project object (except the
	 * auto-generated project ID), prompting the user each time for the desired
	 * information and calling the appropriate method for each type of expected
	 * input.
	 * 
	 * Instantiates a new project object and sets the parameters of the project
	 * object with the validated user input.
	 * 
	 * Calls the addProject method in the ProjectService class with the new project
	 * object as an argument.
	 * 
	 * If the project is successfully added to the database, the user is notified
	 * and the project information is displayed, along with the auto-generated
	 * project id, then the user menu is displayed again.
	 * 
	 * If the project is not successfully created, the exceptions thrown by other
	 * methods will be displayed.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		// Calls validateDifficulty method if difficulty input from user is not null.
		if (Objects.nonNull(difficulty)) {
			validateDifficulty(difficulty);
		}
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	} // end createProject

	// Validates difficulty input to be between 1 and 5 inclusive. If not, the user
	// is prompted to try again.
	private void validateDifficulty(Integer difficulty) {
		if (difficulty < 1 || difficulty > 5) {
			throw new DbException(difficulty + " is not between 1 and 5. ");
		}
	} // end validateDifficulty

	/*
	 * Calls the getListOfProjectNames method in projectService. Receives a List of
	 * available projects in return.
	 * 
	 * Removes currentProject so the project information listing does not clutter
	 * the console.
	 * 
	 * Prints the List of available projects using a Lambda expression.
	 */
	private void getProjectNames() {
		List<Project> projects = projectService.getListOfProjectNames();

		currentProject = null;

		System.out.println("\nAvailable projects:");

		projects.forEach(
				project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	} // end listProjectNames

	/*
	 * Calls the getProjectNames method to display the available projects for the
	 * user.
	 * 
	 * Prompts the user to input the project id they want to see.
	 * 
	 * Removes currentProject so the project information listing does not clutter
	 * the console.
	 * 
	 * Calls the fetchProjectByIdService, passing the selected project id and sets
	 * currentProject to that project.
	 * 
	 * Validates that the current id is an available project to display. If not,
	 * tells the user to try again.
	 * 
	 */
	private void getProjectByIdMain() {
		getProjectNames();

		Integer projectId = getIntInput("Select a project ID to see that project");

		currentProject = null;

		currentProject = projectService.fetchProjectByIdService(projectId);

		if (Objects.isNull(currentProject)) {
			System.out.println("\nThat is not a valid project.");
		}
	} // end selectProject

	/*
	 * Checks to see if a current project is selected. If not, user is prompted to
	 * do so using menu option 3.
	 * 
	 * If current project is selected, calls the appropriate input method for each
	 * project parameter (except project ID, which is in currentProject) and prompts
	 * the user to enter the new value as well as displaying the value of the
	 * parameter in the current project.
	 * 
	 * Instantiates a new project object and sets the project ID from the currently
	 * selected project.
	 * 
	 * Using a ternary conditional operator to determine if the user input for the
	 * parameter is null or not. If the input for a given parameter is null, the
	 * parameter value of the current project is set on the new project object. If
	 * the input is not null, the user input for that parameter is set on the new
	 * project object.
	 * 
	 * Calls modifyProjectDetailsService and passes the new project object.
	 * 
	 * If the modification is successful, fetchProjectByIdService is called to
	 * update the currentProject.
	 */
	private void updateProjectDetails() {
		if (Objects.isNull(currentProject)) {
			System.out.println("\nYou do not have an active project. Choose menu option 3 to select a project");
			return;
		}

		String projectName = getStringInput("Enter the project name [" + currentProject.getProjectName() + "]");
		BigDecimal estimatedHours = getDecimalInput(
				"Enter the estimated hours [" + currentProject.getEstimatedHours() + "]");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + currentProject.getActualHours() + "]");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + currentProject.getDifficulty() + "]");
		// Calls validateDifficulty method if difficulty input from user is not null.
		if (Objects.nonNull(difficulty)) {
			validateDifficulty(difficulty);
		}
		String notes = getStringInput("Enter the project notes [" + currentProject.getNotes() + "]");

		Project project = new Project();

		project.setProjectId(currentProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? currentProject.getProjectName() : projectName);
		project.setEstimatedHours(Objects.isNull(estimatedHours) ? currentProject.getEstimatedHours() : estimatedHours);
		project.setActualHours(Objects.isNull(actualHours) ? currentProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? currentProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? currentProject.getNotes() : notes);

		projectService.modifyProjectDetailsService(project);

		currentProject = projectService.fetchProjectByIdService(currentProject.getProjectId());
	} // end updateProjectDetails

	/*
	 * Calls getProjectNames to display list of available projects.
	 * 
	 * Prompts user for ID of project to delete.
	 * 
	 * If user input is not null, calls deleteProjectService and passes the project
	 * ID the user has selected for deletion.
	 * 
	 * If project is successfully deleted, user is informed that the project has
	 * been deleted.
	 * 
	 * Checks to see if currentProject is not null and is the same project that was
	 * deleted. If so, currentProject is set to null.
	 */
	private void deleteProject() {
		getProjectNames();

		Integer projectId = getIntInput("Enter the ID of the project to delete");

		if (Objects.nonNull(projectId)) {
			projectService.deleteProjectService(projectId);

			System.out.println("Project with ID=" + projectId + " has been deleted");

			if (Objects.nonNull(currentProject) && currentProject.getProjectId().equals(projectId)) {
				currentProject = null;
			}
		}
	} // end deleteProject

	/*
	 * Tells the user that the program is exiting and returns True to
	 * processUserSelection, which ends the while loop and ends the program.
	 */
	private boolean exitMenu() {
		System.out.println("Exiting the application.");
		return true;
	} // end exitMenu

} // end CLASS