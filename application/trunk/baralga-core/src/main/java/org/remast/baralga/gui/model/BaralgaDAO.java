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
		"create table db_version (" +
		"     id           identity," +
		"     version      number," +
		"     created_at   timestamp," +
		"     description  varchar2(255)" +
		"    )";
	static final String versionTableInsert = "insert into db_version (version, description) values (1, 'Initial database setup.')";

	static final String projectTableCreate =  
		"create table project (" +
		"     id           identity," +
		"     title        varchar(255)," +
		"     description  varchar(4000)," +
		"     active       boolean" +
		"    )";

	static final String activityTableCreate =  
		"create table activity (" +
		"     id           identity," +
		"     description  varchar(4000)," +
		"     start        timestamp," +
		"     end          timestamp," +
		"     project_id   number," +
		"     FOREIGN key (project_id) REFERENCES project(id)" +
		"    )";


	/**
	 * @param args
	 */
	public BaralgaDAO() {
	}

	public void init() throws SQLException {
		final String dataDirName = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath();
		connection = DriverManager.getConnection("jdbc:h2:" + dataDirName + "/baralga;DB_CLOSE_ON_EXIT=FALSE", "baralga-user", "");

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
			final PreparedStatement activityDelete = connection.prepareStatement("delete from activity where project_id = ?");
			activityDelete.setLong(1, project.getId());
			activityDelete.execute();

			// Remove the project
			final PreparedStatement projectDelete = connection.prepareStatement("delete from project where id = ?");
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

			PreparedStatement pst = connection.prepareStatement("insert into project (title, description, active) values (?, ?, ?)");

			pst.setString(1, project.getTitle());
			pst.setString(2, project.getDescription());
			pst.setBoolean(3, project.isActive());

			pst.execute();

			ResultSet rs = st.executeQuery("select max(id) as id from project");
			rs.next();
			long id = rs.getLong("id");

			project.setId(id);
		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}
	}

	/**
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getActiveProjects() {
		final List<Project> activeProjects = new ArrayList<Project>();

		try {
			final Statement st = connection.createStatement();

			final ResultSet rs = st.executeQuery("select * from project where active = True");
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description"));
				project.setActive(true);
				activeProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return activeProjects;
	}

	public synchronized List<Project> getAllProjects() {
		final List<Project> activeProjects = new ArrayList<Project>();

		try {
			final Statement st = connection.createStatement();

			final ResultSet rs = st.executeQuery("select * from project");
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description"));
				project.setActive(rs.getBoolean("active"));
				activeProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
			throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
		}

		return activeProjects;
	}

	private void updateDatabase() throws SQLException {
		boolean databaseExists = false;

		final Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("SHOW TABLES");
		while (rs.next()) {
			if ("db_version".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
				databaseExists = true;
				break;
			}
		}

		if (!databaseExists) {
			st.execute(versionTableCreate);
			st.execute(projectTableCreate);
			st.execute(activityTableCreate);
			st.execute(versionTableInsert);
		}
		connection.commit();

		rs = st.executeQuery("select max(version) as version, description from db_version");
		rs.next();
		int version = rs.getInt("version");
		String description = rs.getString("description");

		System.out.println("Using Baralga DB Version: " + version + ", description: " + description);
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

			final ResultSet rs = st.executeQuery("select * from activity, project where activity.project_id = project.id " + filterCondition + " order by start asc");
			while (rs.next()) {
				final Project project = new Project(rs.getLong("project.id"), rs.getString("title"), rs.getString("project.description"));
				project.setActive(rs.getBoolean("active"));

				final ProjectActivity activity = new ProjectActivity(new DateTime(rs.getTimestamp("start")), new DateTime(rs.getTimestamp("end")), project);
				activity.setId(rs.getLong("activity.id"));
				activity.setDescription(rs.getString("activity.description"));
				
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

			final PreparedStatement pst = connection.prepareStatement("insert into activity (description, start, end, project_id) values (?, ?, ?, ?)");

			pst.setString(1, activity.getDescription());

			final Timestamp d = new Timestamp( activity.getStart().getMillis());
			pst.setTimestamp(2,d);

			final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
			pst.setTimestamp(3, endDate);
			
			pst.setLong(4, activity.getProject().getId());
			
			pst.execute();

			final ResultSet  rs = st.executeQuery("select max(id) as id from activity");
			rs.next();
			
			long id = rs.getLong("id");

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
			final PreparedStatement pst = connection.prepareStatement("delete from activity where id = ?");

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

	public void start(DateTime start) {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// TODO Auto-generated method stub

	}

	public void updateProject(Project project) {
		if (project == null) {
			return;
		}

		// TODO: Check if exists
		try {
			final PreparedStatement pst = connection.prepareStatement("update project set title = ?, description = ?, active = ? where id = ?");
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
			final PreparedStatement pst = connection.prepareStatement("update activity set description = ?, start = ?, end = ?, project_id = ? where id = ?");

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
			final PreparedStatement pst = connection.prepareStatement("select * from project where id = ?");
			pst.setLong(1, projectId);

			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				final Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description"));
				project.setActive(rs.getBoolean("active"));
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
			final ResultSet rs = st.executeQuery("select distinct month(activity.start) as month from activity order by month desc");
			while (rs.next()) {
				monthList.add(rs.getInt("month"));
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
			final ResultSet rs = st.executeQuery("select distinct year(activity.start) as year from activity order by year desc");
			while (rs.next()) {
				yearList.add(rs.getInt("year"));
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
			final ResultSet rs = st.executeQuery("select distinct week(activity.start) as week from activity order by week desc");
			while (rs.next()) {
				weekOfYearList.add(rs.getInt("week"));
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
		
		final StringBuilder sqlCondition = new StringBuilder("");
		
		if (filter.getProject() != null && filter.getProject().getId() > 0) {
			sqlCondition.append(" and activity.project_id = '" + filter.getProject().getId() + "' ");
		}
		
		if (filter.getDay() != null) {
			sqlCondition.append(" and day_of_week(activity.start) = " + filter.getDay() + " ");
		}
		
		if (filter.getWeekOfYear() != null) {
			sqlCondition.append(" and week(activity.start) = " + filter.getWeekOfYear() + " ");
		}
		
		if (filter.getMonth() != null) {
			sqlCondition.append(" and month(activity.start) = " + filter.getMonth() + " ");
		}
		
		if (filter.getYear() != null) {
			sqlCondition.append(" and year(activity.start) = " + filter.getYear() + " ");
		}
		
		return getActivities(sqlCondition.toString());
	}

}
