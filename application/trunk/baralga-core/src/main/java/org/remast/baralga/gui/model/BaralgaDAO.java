//---------------------------------------------------------
// $Id$ 
// 
// (c) 2010 Cellent Finance Solutions AG 
//          Calwer Strasse 33 
//          70173 Stuttgart 
//          www.cellent-fs.de 
//--------------------------------------------------------- 
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.remast.baralga.model.Project;
import org.remast.baralga.model.ProjectActivity;

public class BaralgaDAO {

	/** The logger. */
	private static final Log log = LogFactory.getLog(BaralgaDAO.class);

	private Connection conn;

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
		try {
			conn = DriverManager.getConnection("jdbc:h2:~/.ProTrack/data/baralga", "baralga-user", "");
		} catch (SQLException e) {
			log.error(e, e);
		}

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
			PreparedStatement pst = conn.prepareStatement("delete from project where id = ?");

			pst.setLong(1, project.getId());

			pst.execute();
		} catch (SQLException e) {
			log.error(e, e);
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
			st = conn.createStatement();

			PreparedStatement pst = conn.prepareStatement("insert into project (title, description, active) values (?, ?, ?)");

			pst.setString(1, project.getTitle());
			pst.setString(2, project.getDescription());
			pst.setBoolean(3, project.isActive());

			pst.execute();

			ResultSet  rs = st.executeQuery("select max(id) as id from project");
			rs.next();
			long id = rs.getLong("id");

			project.setId(id);
		} catch (SQLException e) {
			log.error(e, e);
		}
	}

	/**
	 * @return read-only view of the projects
	 */
	public synchronized List<Project> getActiveProjects() {
		final List<Project> activeProjects = new ArrayList<Project>();

		Statement st;
		try {
			st = conn.createStatement();

			ResultSet rs = st.executeQuery("select * from project where active = True");
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description"));
				project.setActive(true);
				activeProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
		}

		return activeProjects;
	}

	public synchronized List<Project> getAllProjects() {
		final List<Project> activeProjects = new ArrayList<Project>();

		Statement st;
		try {
			st = conn.createStatement();

			ResultSet rs = st.executeQuery("select * from project");
			while (rs.next()) {
				Project project = new Project(rs.getLong("id"), rs.getString("title"), rs.getString("description"));
				project.setActive(rs.getBoolean("active"));
				activeProjects.add(project);
			}

		} catch (SQLException e) {
			log.error(e, e);
		}

		return activeProjects;
	}

	private void updateDatabase() {
		boolean databaseExists = false;

		Statement st;
		try {
			st = conn.createStatement();

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
			conn.commit();

			rs = st.executeQuery("select max(version) as version, description from db_version");
			rs.next();
			int version = rs.getInt("version");
			String description = rs.getString("description");

			System.out.println("Using Baralga DB Version: " + version + ", description: " + description);
		} catch (SQLException e) {
			log.error(e, e);
		}
	}

	/**
	 * @return read-only view of the activities
	 */
	public List<ProjectActivity> getActivities() {
		final List<ProjectActivity> activities = new ArrayList<ProjectActivity>();

		Statement st;
		try {
			st = conn.createStatement();

			ResultSet rs = st.executeQuery("select * from activity, project where activity.project_id = project.id");
			while (rs.next()) {
				Project project = new Project(rs.getLong("project.id"), rs.getString("title"), rs.getString("description"));
				project.setActive(rs.getBoolean("active"));
				
				ProjectActivity activity = new ProjectActivity(new DateTime(rs.getTimestamp("start")), new DateTime(rs.getTimestamp("end")), project);
				activity.setId(rs.getLong("activity.id"));
				
				activities.add(activity);
			}

		} catch (SQLException e) {
			log.error(e, e);
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
		Statement st;
		try {
			st = conn.createStatement();

			PreparedStatement pst = conn.prepareStatement("insert into activity (description, start, end, project_id) values (?, ?, ?, ?)");

			pst.setString(1, activity.getDescription());

			final Timestamp d = new Timestamp( activity.getStart().getMillis());
			pst.setTimestamp(2,d);

			final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
			pst.setTimestamp(3, endDate);

			pst.setLong(4, activity.getProject().getId());

			pst.execute();

			ResultSet  rs = st.executeQuery("select max(id) as id from activity");
			rs.next();
			long id = rs.getLong("id");

			activity.setId(id);
		} catch (SQLException e) {
			log.error(e, e);
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
			PreparedStatement pst = conn.prepareStatement("delete from activity where id = ?");

			pst.setLong(1, activity.getId());

			pst.execute();
		} catch (SQLException e) {
			log.error(e, e);
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
			PreparedStatement pst = conn.prepareStatement("update project set title = ?, description = ?, active = ? where id = ?");

			pst.setString(1, project.getTitle());
			pst.setString(2, project.getDescription());
			pst.setBoolean(3, project.isActive());
			pst.setLong(4, project.getId());

			pst.execute();
		} catch (SQLException e) {
			log.error(e, e);
		}
	}

	public void updateActivity(ProjectActivity activity) {
		if (activity == null) {
			return;
		}

		// TODO: Check if exists
		try {
			PreparedStatement pst = conn.prepareStatement("update activity set description = ?, start = ?, end = ?, project_id = ? where id = ?");

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
		}
		
	}

}
