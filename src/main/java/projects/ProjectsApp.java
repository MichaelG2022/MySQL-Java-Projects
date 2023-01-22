package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	// Instantiates a scanner object to get user input.
	private Scanner scanner = new Scanner(System.in);

	// Instantiates a projectService object.
	private ProjectService projectService = new ProjectService();

	// Creates a list of possible menu choices for the user.
	// @formatter:off
	private List<String> operations = List.of(
		"1) Add a project"
	); // end operations
	// @formatter:on

	// Calls the processUerSelection.
	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();
	} // end MAIN

	/*
	 * Uses a while loop to keep the program running until the user quits the
	 * program by hitting Enter key on blank line, which calls the exitMenu method
	 * to stop the program. Calls the getUserSelection method to get the user input
	 * and uses a switch to determine what the user wants to do next. A non-integer
	 * input throws an exception and prompts the user to try again. An integer input
	 * that is not included in the menu causes the Switch default option to tell the
	 * user about the invalid selection and prompts to try again. If the user wants
	 * to create a project, the createProject method is called.
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
				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
				} // end SWITCH
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		} // end WHILE
	} // end processUserSelection

	/*
	 * Calls the printOperations method to display the user selection menu, then
	 * starts the process of getting the user input and validating it by calling the
	 * getIntInput method with the menu prompt. If the return from the getIntInput
	 * is null, a -1 is returned to the processUserSelection method, which causes
	 * the program to exit, otherwise returns the user input to the
	 * processUserSelection method.
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	} // end getUserSelection

	/*
	 * Displays the menu header then the menu choices by using Lambda on the
	 * operations List<> created above.
	 */
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println("  " + line));
	} // end printOperations

	/*
	 * Calls the getStringInput method with the menu prompt to capture the user menu
	 * selection then attempts to validate the input as an Integer. If the input is
	 * not an Integer, an exception is thrown and the user is allowed to try again,
	 * starting over from the main menu. If the input is null, meaning the user hit
	 * Enter on a blank line to quit, a null is returned to getUserSelection. If the
	 * input is valid, it is returned to the getUserSelection method.
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
	 * Displays the menu prompt and uses a scanner to capture the user input line,
	 * then uses a ternary conditional operator to return null if the input is blank
	 * or the trimmed user input to getIntInput.
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String line = scanner.nextLine();

		return line.isBlank() ? null : line.trim();
	} // end getStringInput

	/*
	 * Calls getStringInput with a prompt to get user input. Returns null if the
	 * input is null, otherwise returns the user input if it is validated as a
	 * decimal number. If the user input is not a valid decimal number, an exception
	 * is thrown and the user is prompted to start again from the beginning.
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
	 * for each type of expected input. Creates a new project object and sets the
	 * parameters of the project object with the user input. Calls the addProject
	 * method in the ProjectService class with the just-created project object as an
	 * argument. If the project is successfully added to the database, the user is
	 * notified and the project information is displayed, along with the
	 * auto-generated project id, then the user menu is displayed again. If the
	 * project is not successfully created, the exceptions thrown will be displayed
	 * from other classes.
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
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

	/*
	 * Tells the user that the program is exiting and returns True to
	 * processUserSelection, which ends the while loop and ends the program.
	 */
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	} // end exitMenu

} // end CLASS
