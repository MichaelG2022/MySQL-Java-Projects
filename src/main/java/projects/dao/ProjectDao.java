package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
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
	 * ProjectService class.
	 * 
	 * Creates the SQL string statement for inserting the project into the database.
	 * Uses ? placeholders for the parameters of the project object.
	 * 
	 * Uses try-with-resources to make the connection to the database using the
	 * getConnection method in the DbConnection class. If the connection fails, an
	 * exception is thrown.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase
	 * class.
	 * 
	 * Inside the connection Try, uses try-with-resources for the SQL Prepared
	 * Statement, then sets parameters for the project object, then attempts to
	 * execute the update on the database.
	 * 
	 * Retrieves the auto-generated project id from the getLastInsertId method in
	 * the DaoBase class, then commits the transaction if all is successful, then
	 * sets the project id in the project object
	 * 
	 * If any exception is thrown in the Prepared Statement portion, the entire
	 * transaction is rolled back using the rollbackTransaction method in the
	 * DaoBase class.
	 * 
	 * If any exception is thrown in the Connection Try block, an SQL exception is
	 * thrown.
	 * 
	 * Returns the project object if everything succeeds.
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

	/*
	 * Creates the SQL string statement for selecting all projects in the database.
	 * 
	 * Uses try-with-resources to make the connection to the database using the
	 * getConnection method in the DbConnection class. If the connection fails, an
	 * exception is thrown.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase
	 * class.
	 * 
	 * Inside the connection Try, uses try-with-resources for the SQL Prepared
	 * Statement.
	 * 
	 * Inside the Prepared Statement Try, uses try-with-resources to get the Result
	 * Set and execute the query on the database.
	 * 
	 * Creates a List of projects and retrieves each project, then adds the
	 * extracted project to the List.
	 * 
	 * If any exception is thrown in the Prepared Statement portion, the entire
	 * transaction is rolled back using the rollbackTransaction method in the
	 * DaoBase class.
	 * 
	 * If any exception is thrown in the Connection Try block, an SQL exception is
	 * thrown.
	 * 
	 * Returns the List of projects if everything succeeds.
	 */
	public List<Project> getAllProjectNames() {
		// @formatter:off
		String sql = ""
			+ "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";
		// formatter:on
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				try(ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();
					
					while (rs.next()) {
						projects.add(extract(rs, Project.class));
					}
					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}			
		} catch (SQLException e) {
			throw new DbException(e);
		}
	} // end getAllProjectNames
	
	
	/*
	 * Creates the SQL string statement for selecting a project by project_id using a ? placeholder.
	 * 
	 * Uses try-with-resources to make the connection to the database using the
	 * getConnection method in the DbConnection class. If the connection fails, an
	 * exception is thrown.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase class.
	 * 
	 * Inside the connection Try, uses a Try/Catch block to create a project object and set it to null.
	 * The object is used an an Optional object for Optional.ofNullable().
	 * 
	 * Inside the Optional object Try, uses try-with-resources for the SQL Prepared Statement.
	 * Sets the project_id parameter for the ? placeholder.
	 * 
	 * Uses try-with-resources to get the Result Set and execute the query on the
	 * database.
	 * 
	 * Inside the Result Set try, creates a project object and retrieves the correct extracted project object.
	 * 
	 * Once the project matching the project_id is found (nonNull object), the getMaterials, getSteps,
	 * and getCategories methods are called to add all the data for the correct project to the project object.
	 * 
	 * If everything is successful, the transaction is committed and the Optional.ofNullable project object
	 * is returned. 
	 * 
	 * If any exception is thrown in the Optional object portion, the transaction is rolled back using
	 * the rollbackTransaction method in the DaoBase class.
	 * 
	 * If any exception is thrown in the Connection Try block, an SQL exception is thrown.
	 * 
	 * Returns the List of projects if everything succeeds.
	 */
	public Optional<Project> fetchProjectByIdDao(Integer projectId) {
		String sql = "Select * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try(Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			
			try {
				Project project = null;
				
				try(PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);
					
					try(ResultSet rs = stmt.executeQuery()) {
						if(rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if(Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				}
				
				commitTransaction(conn);
				return Optional.ofNullable(project);
				
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	} // end fetchProjectByIdDao
		
	/*
	 * This method throws the SQLException set up in the try-with-resources of the method that called it.
	 * 
	 * Creates the SQL string statement for selecting a project's material from the material table
	 * by project_id using a ? placeholder.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase class.
	 * 
	 * Uses the connection from the calling method.
	 * 
	 * Uses try-with-resources for the SQL Prepared Statement. Sets the project_id parameter for the ? placeholder.
	 * 
	 * Uses try-with-resources to get the Result Set and execute the query on the database.
	 * 
	 * Inside the Result Set try, creates a List of Material and adds the extracted material object
	 * to the List.
	 * 
	 * Once all the materials are added, the List is returned to the calling class.
	 */
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
					
			try(ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();
						
				while(rs.next()) {
					materials.add(extract(rs, Material.class));
				}
				return materials;
			}
		}
	} // end fetchMaterialsForProject
	
	/*
	 * This method throws the SQLException set up in the try-with-resources of the method that called it.
	 * 
	 * Creates the SQL string statement for selecting a project's steps from the step table
	 * by project_id using a ? placeholder.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase class.
	 * 
	 * Uses the connection from the calling method.
	 * 
	 * Uses try-with-resources for the SQL Prepared Statement. Sets the project_id parameter for the ? placeholder.
	 * 
	 * Uses try-with-resources to get the Result Set and execute the query on the database.
	 * 
	 * Inside the Result Set try, creates a List of Step and adds the extracted step object
	 * to the List.
	 * 
	 * Once all the steps are added, the List is returned to the calling class.
	 */
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";
				
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
					
			try(ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();
						
				while(rs.next()) {
					steps.add(extract(rs, Step.class));
				}
				return steps;
			}
		}
	} // end fetchStepsForProject
		
	/*
	 * This method throws the SQLException set up in the try-with-resources of the method that called it.
	 * 
	 * Creates the SQL string statement for selecting a project's categories from the category table
	 *  joined with the project_category table by project_id using a ? placeholder.
	 * 
	 * Starts the transaction using the startTransaction method in the DaoBase class.
	 * 
	 * Uses the connection from the calling method.
	 * 
	 * Uses try-with-resources for the SQL Prepared Statement. Sets the project_id parameter for the ? placeholder.
	 * 
	 * Uses try-with-resources to get the Result Set and execute the query on the database.
	 * 
	 * Inside the Result Set try, creates a List of Category and adds the extracted category object
	 * to the List.
	 * 
	 * Once all the materials are added, the List is returned to the calling class.
	 */
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		// formatter:off
		String sql = ""
			+ "Select c.* FROM " + CATEGORY_TABLE + " c "
			+ "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
			+ "WHERE project_id = ?";
		// formatter:on
		
		try(PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);
			
			try(ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();
				
				while(rs.next()) {
					categories.add(extract(rs, Category.class));
				}
				return categories;
			}
		}		
	} // end fetchCategoriesForProject
	
} // end CLASS