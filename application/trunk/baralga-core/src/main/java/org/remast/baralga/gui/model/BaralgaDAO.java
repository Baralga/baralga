package org.remast.baralga.gui.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.remast.baralga.gui.BaralgaMain;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.model.filter.Filter;
import org.remast.util.TextResourceBundle;

/**
 * Reads and writes all objects to the database and maintains the database connection.
 * @author remast
 */
public class BaralgaDAO {

	/** The bundle for internationalized texts. */
	private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(BaralgaMain.class);

	/** The logger. */
	private static final Log log = LogFactory.getLog(BaralgaDAO.class);

	private Connection connection;

	static final String versionTableCreate =  
		"create table db_version (" + //$NON-NLS-1$
		"     id           identity," + //$NON-NLS-1$
		"     version      number," + //$NON-NLS-1$
		"     created_at   timestamp," + //$NON-NLS-1$
		"     description  varchar2(255)" + //$NON-NLS-1$
		"    )"; //$NON-NLS-1$
	static final String versionTableInsert = "insert into db_version (version, description) values (1, 'Initial database setup.')"; //$NON-NLS-1$

	static final String projectTableCreate =  
		"create table project (" + //$NON-NLS-1$
		"     id           identity," + //$NON-NLS-1$
		"     title        varchar(255)," + //$NON-NLS-1$
		"     description  varchar(4000)," + //$NON-NLS-1$
		"     active       boolean" + //$NON-NLS-1$
		"    )"; //$NON-NLS-1$

	static final String activityTableCreate =  
		"create table activity (" + //$NON-NLS-1$
		"     id           identity," + //$NON-NLS-1$
		"     description  varchar(4000)," + //$NON-NLS-1$
		"     start        timestamp," + //$NON-NLS-1$
		"     end          timestamp," + //$NON-NLS-1$
		"     project_id   number," + //$NON-NLS-1$
		"     FOREIGN key (project_id) REFERENCES project(id)" + //$NON-NLS-1$
		"    )"; //$NON-NLS-1$


	/**
	 * @param args
	 */
	public BaralgaDAO() {
	}

	public void init() throws SQLException {
		final String dataDirName = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath();
		connection = DriverManager.getConnection("jdbc:h2:" + dataDirName + "/baralga;DB_CLOSE_ON_EXIT=FALSE", "baralga-user", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		updateDatabase();
	}

	/**
	 * Removes a project.
	 * @param project the project to remove
	 */
	public synchronized void remove(final Project project) {
		if (project == null) {
			return;
		}

		try {
			// Remove activities associated with the project
			final PreparedStatement activityDelete = connection.prepareStatement("delete from activity where project_id = ?"); //$NON-NLS-1$
			activityDelete.setLong(1, project.getId());
			activityDelete.execute();

			// Remove the project
			final PreparedStatement projectDelete = connection.prepareStatement("delete from project where id = ?"); //$NON-NLS-1$
			projectDelete.setLong(1, project.getId());
			projectDelete.execute();
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}


	/**
	 * Adds a new project.
	 * @param project the project to add
	 */
	public void addProject(final Project project) {
		if (project == null) {
			return;
		}

		// TODO: Check if exists
		Statement st;
		try {
			st = connection.createStatement();

			PreparedStatement pst = connection.prepareStatement("insert into project (title, description, active) values (?, ?, ?)"); //$NON-NLS-1$

			pst.setString(1, project.getTitle());
			pst.setString(2, project.getDescription());
			pst.setBoolean(3, project.isActive());

			pst.execute();

			ResultSet rs = st.executeQuery("select max(id) as id from project"); //$NON-NLS-1$
			rs.next();
			long id = rs.getLong("id"); //$NON-NLS-1$

			project.setId(id);
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Getter for all active projects.
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getActiveProjects() {
		final List<Project> activeProjects = new ArrayList<Project>();

		try {
			final Statement st = connection.createStatement();

			final ResultSet rs = st.executeQuery("select * from project where active = True"); //$NON-NLS-1$
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				project.setActive(true);
				activeProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return Collections.unmodifiableList(activeProjects);
	}

	/**
	 * Getter for all projects (both active and inactive).
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getAllProjects() {
		final List<Project> allProjects = new ArrayList<Project>();

		try {
			final Statement st = connection.createStatement();

			final ResultSet rs = st.executeQuery("select * from project"); //$NON-NLS-1$
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				project.setActive(rs.getBoolean("active")); //$NON-NLS-1$
				allProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return Collections.unmodifiableList(allProjects);
	}

	private void updateDatabase() throws SQLException {
		boolean databaseExists = false;

		final Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SHOW TABLES"); //$NON-NLS-1$
		while (rs.next()) {
			if ("db_version".equalsIgnoreCase(rs.getString("TABLE_NAME"))) { //$NON-NLS-1$ //$NON-NLS-2$
				databaseExists = true;
				break;
			}
		}

		if (!databaseExists) {
			log.info("Creating Baralga DB."); //$NON-NLS-1$
			st.execute(versionTableCreate);
			st.execute(projectTableCreate);
			st.execute(activityTableCreate);
			st.execute(versionTableInsert);
		}
		connection.commit();

		rs = st.executeQuery("select max(version) as version, description from db_version"); //$NON-NLS-1$
		rs.next();
		int version = rs.getInt("version"); //$NON-NLS-1$
		String description = rs.getString("description"); //$NON-NLS-1$

		log.info("Using Baralga DB Version: " + version + ", description: " + description); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public List<ProjectActivity> getActivities() {
		final String filterCondition = StringUtils.EMPTY;
		return getActivities(filterCondition);
	}

	/**
	 * @return read-only view of the activities
	 */
	public List<ProjectActivity> getActivities(final String condition) {
		final List<ProjectActivity> activities = new ArrayList<ProjectActivity>();

		try {
			final Statement st = connection.createStatement();
			
			final String filterCondition = StringUtils.defaultString(condition);

			final ResultSet rs = st.executeQuery("select * from activity, project where activity.project_id = project.id " + filterCondition + " order by start asc"); //$NON-NLS-1$ //$NON-NLS-2$
			while (rs.next()) {
				final Project project = new Project(rs.getLong("project.id"), rs.getString("title"), rs.getString("project.description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				project.setActive(rs.getBoolean("active")); //$NON-NLS-1$

				final ProjectActivity activity = new ProjectActivity(new DateTime(rs.getTimestamp("start")), new DateTime(rs.getTimestamp("end")), project); //$NON-NLS-1$ //$NON-NLS-2$
				activity.setId(rs.getLong("activity.id")); //$NON-NLS-1$
				activity.setDescription(rs.getString("activity.description")); //$NON-NLS-1$
				
				activities.add(activity);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return activities;
	}

	/**
	 * Adds a new activity.
	 */
	public void addActivity(final ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		// TODO: Check if exists
		try {
			final Statement st = connection.createStatement();

			final PreparedStatement pst = connection.prepareStatement("insert into activity (description, start, end, project_id) values (?, ?, ?, ?)"); //$NON-NLS-1$

			pst.setString(1, activity.getDescription());

			final Timestamp d = new Timestamp( activity.getStart().getMillis());
			pst.setTimestamp(2,d);

			final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
			pst.setTimestamp(3, endDate);
			
			pst.setLong(4, activity.getProject().getId());
			
			pst.execute();

			final ResultSet  rs = st.executeQuery("select max(id) as id from activity"); //$NON-NLS-1$
			rs.next();
			
			long id = rs.getLong("id"); //$NON-NLS-1$

			activity.setId(id);
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Removes an activity.
	 */
	public void removeActivity(final ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		try {
			final PreparedStatement pst = connection.prepareStatement("delete from activity where id = ?"); //$NON-NLS-1$

			pst.setLong(1, activity.getId());

			pst.execute();
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	/**
	 * Adds a bunch of activities.
	 */
	public void addActivities(final Collection<ProjectActivity> activities) {
		for (ProjectActivity activity : activities) {
			addActivity(activity);
		}
	}

	/**
	 * Removes a bunch of activities.
	 */
	public void removeActivities(final Collection<ProjectActivity> activities) {
		for (ProjectActivity activity : activities) {
			removeActivity(activity);
		}
	}

	public void updateProject(Project project) {
		if (project == null) {
			return;
		}

		// TODO: Check if exists
		try {
			final PreparedStatement pst = connection.prepareStatement("update project set title = ?, description = ?, active = ? where id = ?"); //$NON-NLS-1$
			pst.setString(1, project.getTitle());
			pst.setString(2, project.getDescription());
			pst.setBoolean(3, project.isActive());
			pst.setLong(4, project.getId());

			pst.execute();
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	public void updateActivity(ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		// TODO: Check if exists
		try {
			final PreparedStatement pst = connection.prepareStatement("update activity set description = ?, start = ?, end = ?, project_id = ? where id = ?"); //$NON-NLS-1$

			pst.setString(1, activity.getDescription());

			final Timestamp d = new Timestamp( activity.getStart().getMillis());
			pst.setTimestamp(2,d);

			final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
			pst.setTimestamp(3, endDate);

			pst.setLong(4, activity.getProject().getId());
			pst.setLong(5, activity.getId());

			pst.execute();

		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	public Project findProjectById(Long projectId) {
		if (projectId == null) {
			return null;
		}

		try {
			final PreparedStatement pst = connection.prepareStatement("select * from project where id = ?"); //$NON-NLS-1$
			pst.setLong(1, projectId);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				final Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				project.setActive(rs.getBoolean("active")); //$NON-NLS-1$
				return project;
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return null;
	}

	public void close() {
		try {
			if (connection != null && connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			log.error(e, e);
		}
	}
	
	public List<Integer> getMonthList() {
		final List<Integer> monthList = new ArrayList<Integer>();
		
		try {
			final Statement st = connection.createStatement();
			final ResultSet rs = st.executeQuery("select distinct month(activity.start) as month from activity order by month desc"); //$NON-NLS-1$
			while (rs.next()) {
				monthList.add(rs.getInt("month")); //$NON-NLS-1$
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
		
		return monthList;
	}
	
	public List<Integer> getYearList() {
		final List<Integer> yearList = new ArrayList<Integer>();
		
		try {
			final Statement st = connection.createStatement();
			final ResultSet rs = st.executeQuery("select distinct year(activity.start) as year from activity order by year desc"); //$NON-NLS-1$
			while (rs.next()) {
				yearList.add(rs.getInt("year")); //$NON-NLS-1$
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
		
		return yearList;
	}
	
	public List<Integer> getWeekOfYearList() {
		final List<Integer> weekOfYearList = new ArrayList<Integer>();
		
		try {
			final Statement st = connection.createStatement();
			final ResultSet rs = st.executeQuery("select distinct week(activity.start) as week from activity order by week desc"); //$NON-NLS-1$
			while (rs.next()) {
				weekOfYearList.add(rs.getInt("week")); //$NON-NLS-1$
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
		
		return weekOfYearList;
	}

	public List<ProjectActivity> loadActivities(final Filter filter) {
		if (filter == null) {
			return getActivities();
		}
		
		final StringBuilder sqlCondition = new StringBuilder(""); //$NON-NLS-1$
		
		if (filter.getProject() != null && filter.getProject().getId() > 0) {
			sqlCondition.append(" and activity.project_id = '" + filter.getProject().getId() + "' "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (filter.getDay() != null) {
			sqlCondition.append(" and day_of_week(activity.start) = " + filter.getDay() + " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (filter.getWeekOfYear() != null) {
			sqlCondition.append(" and week(activity.start) = " + filter.getWeekOfYear() + " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (filter.getMonth() != null) {
			sqlCondition.append(" and month(activity.start) = " + filter.getMonth() + " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (filter.getYear() != null) {
			sqlCondition.append(" and year(activity.start) = " + filter.getYear() + " "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		return getActivities(sqlCondition.toString());
	}

}
