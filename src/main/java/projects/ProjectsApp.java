package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// Instantiates a Scanner object to get user input.
	private Scanner scanner = new Scanner(System.in);

	// Instantiates a projectService object.
	private ProjectService projectService = new ProjectService();

	// Instantiates a Project object.
	private Project currentProject;

	// Creates a List of possible menu choices for the user.
	// @formatter:off
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List the available projects",
		"3) Select a project to view",
		"99) Display the menu"
	); // end operations
	// @formatter:on

	// Calls the processUerSelection method.
	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();
	} // end MAIN

	/*
	 * Uses a while loop to keep the program running until the user quits the program by
	 * hitting the Enter key on a blank line, which calls the exitMenu method to stop the program.
	 * 
	 * Calls the getUserSelection method to get the user input
	 * and uses a switch to determine what the user wants to do next.
	 * 
	 * A non-integer input throws an exception and prompts the user to try again.
	 * 
	 * An integer input that is not included in the menu causes the Switch default option to tell the
	 * user the selection is not valid and prompts them to try again.
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

				// breaks out of the processUserSelection while loop to redisplay the menu at user's request	
				case 99:
					// Clears currentProject to prevent listing of project again until user chooses to do so.
					currentProject = null;
					break;

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
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
	 * Uses a ternary conditional operator to return -1 is returned to the processUserSelection method,
	 * which causes the program to exit, otherwise it returns the user input to the processUserSelection method.
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput(
				"\nEnter a menu choice or press the Enter key to quit. Enter 99 to display menu choices again");

		return Objects.isNull(input) ? -1 : input;
	} // end getUserSelection

	/*
	 * Displays the menu header then the menu choices by using Lambda expression on the
	 * operations List<> created above.
	 */
	private void printOperations() {
		System.out.println("\n  Menu choices:");

		operations.forEach(line -> System.out.println("  " + line));

		if (Objects.isNull(currentProject)) {
			System.out.println("\nYou do not have an active project.");
		} else {
			System.out.println("\n You are viewing: " + currentProject);
		}
	} // end printOperations

	/*
	 * Calls the getStringInput method with the user prompt to capture the user input
	 * selection then attempts to validate the returned input value as an Integer.
	 *
 	 * If the user hits the Enter key on a blank line to quit, a null is returned to the calling method.
 	 * 
	 * If the input is not an Integer, an exception is thrown and the user is prompted to try again.
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
	 * Uses a ternary conditional operator to return null if the input is blank,
	 * or the trimmed user input to the calling method.
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	} // end getStringInput

	/*
	 * Calls the getStringInput method with the user prompt to capture the user input
	 * selection then attempts to validate the returned input value as a decimal.
	 *
 	 * If the user hits the Enter key on a blank line to quit, a null is returned to the calling method.
 	 * 
	 * If the input is not a decimal, an exception is thrown and the user is prompted to try again.
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
	 * Captures the user input for each parameter in a project object, prompting the
	 * user each time for the desired information and calling the appropriate method
	 * for each type of expected input.
	 * 
	 * Creates a new project object and sets the parameters of the project object with
	 * the validated user input.
	 * 
	 * Calls the addProject method in the ProjectService class with the new project object as an
	 * argument.
	 * 
	 * If the project is successfully added to the database, the user is notified and
	 * the project information is displayed, along with the auto-generated project id,
	 * then the user menu is displayed again.
	 * 
	 * If the project is not successfully created, the exceptions thrown by other methods will be displayed.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		validateDifficulty(difficulty);
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

	// Validates difficulty input to be between 1 and 5 inclusive. If not, the user is prompted to try again.
	private void validateDifficulty(Integer difficulty) {
		if (difficulty < 1 || difficulty > 5) {
			// Removes current project so it is not displayed again
			currentProject = null;
			throw new DbException(difficulty + " is not between 1 and 5. ");
		}
	} // end validateDifficulty

	/*
	 * Calls the getListOfProjectNames method in projectService. Receives a List of available projects in return.
	 * 
	 * Prints the List of available projects using Lambda.
	 */
	private void getProjectNames() {
		List<Project> projects = projectService.getListOfProjectNames();

		System.out.println("\nAvailable projects:");

		projects.forEach(
				project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));

		//return projects;
	} // end listProjectNames
	
	/*
	 * Calls the getProjectNames method to display the available projects for the user.
	 * 
	 * Prompts the user to input the project id they want to see. Resets the current project selection.
	 * 
	 * Calls the fetchProjectByIdService, passing the selected project id.
	 * 
	 * Validates that the current id is an available project to display. If not, tells the user to try again.
	 * 
	 */
	private void getProjectByIdMain() {
		getProjectNames();

		Integer projectId = getIntInput("Select a project ID to see that project");

		// Deselect the currently selected project;
		currentProject = null;

		currentProject = projectService.fetchProjectByIdService(projectId);

		if (Objects.isNull(currentProject)) {
			System.out.println("\nThat is not a valid project.");
		}
	} // end selectProject

	/*
	 * Tells the user that the program is exiting and returns True to
	 * processUserSelection, which ends the while loop and ends the program.
	 */
	private boolean exitMenu() {
		System.out.println("Exiting the application.");
		return true;
	} // end exitMenu

} // end CLASS