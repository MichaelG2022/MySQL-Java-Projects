package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	// CONSTANTS
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	/*
	 * Receives the newly-entered project object from the addProject method in the
	 * ProjectService class. Creates the SQL string statement for inserting the
	 * project into the database. Uses placeholders for the parameters of the
	 * project object. Uses try-with-resources to make the connection to the
	 * database using the getConnection method in the DbConnection class. If the
	 * connection fails, an exception is thrown. Starts the connection using the
	 * startTransaction method in the DaoBase class. Uses try-with-resources to set
	 * parameters for the SQL Prepared Statement. Attempts to execute the update on
	 * the database. Retrieves the auto-generated project id from the
	 * getLastInsertId method in the DaoBase class, then sets the project id in the
	 * project object. If any exception is thrown in the Prepared Statement portion,
	 * the entire transaction is rolled back using the rollbackTransaction method in
	 * the DaoBase class.
	 */
	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+ "VALUE "
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				// Fetch auto-generated project_id using the getLastInsertId method in DaoBase.
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}

	} // end insertProject

}
