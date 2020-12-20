package org.remast.baralga.repository.file;

import org.apache.commons.lang3.StringUtils;
import org.h2.tools.RunScript;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;
import org.remast.baralga.gui.settings.ApplicationSettings;
import org.remast.baralga.model.ProjectActivity;
import org.remast.baralga.repository.ActivityVO;
import org.remast.baralga.repository.BaralgaRepository;
import org.remast.baralga.repository.FilterVO;
import org.remast.baralga.repository.ProjectVO;
import org.remast.util.TextResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;

public class BaralgaFileRepository implements BaralgaRepository {

    /** The bundle for internationalized texts. */
    private static final TextResourceBundle textBundle = TextResourceBundle.getBundle(ProjectActivity.class);

    /** The logger. */
    private static final Logger log = LoggerFactory.getLogger(BaralgaFileRepository.class);

    /**
     * The latest version of the database. If the database is not up to date yet it is being
     * updated to that version.
     */
    public static final int LATEST_DATABASE_VERSION = 3;

    /** The connection to the database. */
    private Connection connection;

    /** The current version number of the database. */
    private int databaseVersion;

    /**
     * Initializes the database and the connection to the database.
     * @throws SQLException on error during initialization
     */
    public void initialize() {
        try {
            final String dataDirPath = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath();
            init(dataDirPath);
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    void init(String dataDirPath) throws SQLException {
        String separator = dataDirPath.equalsIgnoreCase("mem") ? ":" : "/";
        connection = DriverManager.getConnection("jdbc:h2:" + dataDirPath + separator + "baralga;DB_CLOSE_ON_EXIT=FALSE", "baralga-user", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        // Perform an update if necessary.
        updateDatabase();
    }

    /**
     * Closes the database by closing the only connection to it.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Updates or creates the database. If the database is empty it will be setup
     * otherwise an existing database will be updated.
     * @throws SQLException on error during update
     */
    void updateDatabase() throws SQLException {
        boolean databaseExists = false;

        // Look for table db_version if that is present the database has already been set up
        try (final Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SHOW TABLES")) { //$NON-NLS-1$
                while (resultSet.next()) {
                    if ("db_version".equalsIgnoreCase(resultSet.getString("TABLE_NAME"))) { //$NON-NLS-1$ //$NON-NLS-2$
                        databaseExists = true;
                        break;
                    }
                }
            }

            if (!databaseExists) {
                log.info("Creating Baralga DB."); //$NON-NLS-1$
                executeScript("setup_database.sql");
                log.info("Baralga DB successfully created."); //$NON-NLS-1$
            }
            connection.commit();

            databaseVersion = -1;
            String description = "-"; //$NON-NLS-1$
            try (ResultSet resultSet = statement.executeQuery("select version, description from db_version order by version desc limit 1")) { //$NON-NLS-1$
                if (resultSet.next()) {
                    databaseVersion = resultSet.getInt("version"); //$NON-NLS-1$
                    description = resultSet.getString("description"); //$NON-NLS-1$
                }
            }

            int versionDifference = LATEST_DATABASE_VERSION - databaseVersion;
            for (int i = 1; i <= versionDifference; i++) {
                final int versionUpdate = databaseVersion + i;
                log.info("Updating database to version " + versionUpdate + "."); //$NON-NLS-1$
                final String updateScript = "db_version_" + StringUtils.leftPad(String.valueOf(versionUpdate), 3, "0") + ".sql";
                executeScript(updateScript);
            }

            log.info("Using Baralga DB Version: {}, description: {}.", databaseVersion, description); //$NON-NLS-1$
        }
    }

    /**
     * Executes an sql script.
     * @param scriptName the name of the script to be executed
     * @throws SQLException on errors during execution
     */
    private void executeScript(final String scriptName) throws SQLException {
        log.info("Executing sql script " +  scriptName + ".");

        if (StringUtils.isBlank(scriptName)) {
            return;
        }

        InputStream is = BaralgaFileRepository.class.getResourceAsStream("sql/h2/" + scriptName);
        Reader reader = new InputStreamReader(is);
        RunScript.execute(connection, reader);
    }

    /**
     * Prepares a statement with the given sql string.
     * @param sql the sql of the statement
     * @return the prepared statement
     * @throws SQLException on errors during statement creation
     */
    private PreparedStatement prepare(final String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    /**
     * Removes a project.
     * @param project the project to remove
     */
    public void remove(final ProjectVO project) {
        if (project == null) {
            return;
        }

        final String sql = "delete from activity where project_id = ?"; //$NON-NLS-1$
        try (final PreparedStatement activityDelete = prepare(sql)) {
            // Remove activities associated with the project
            activityDelete.setObject(1, project.getId());
            activityDelete.execute();

            // Remove the project
            try (final PreparedStatement projectDelete = prepare("delete from project where project_id = ?")) { //$NON-NLS-1$
                projectDelete.setObject(1, project.getId());
                projectDelete.execute();
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Adds a new project.
     * @param project the project to add
     */
    public ProjectVO addProject(final ProjectVO project) {
        if (project == null) {
            return null;
        }

        if (project.getId() == null) {
            throw new IllegalArgumentException("Cannot add project without id.");
        }

        // TODO: Check if exists
        final String sql = "insert into project (project_id, title, description, active) values (?, ?, ?, ?)"; //$NON-NLS-1$
        final String id = UUID.randomUUID().toString();
        try (final PreparedStatement preparedStatement = prepare(sql)) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, project.getTitle());
            preparedStatement.setString(3, project.getDescription());
            preparedStatement.setBoolean(4, project.isActive());

            preparedStatement.execute();

            project.setId(id);
            return project;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Getter for all active projects.
     * @return read-only view of the projects
     */
    public List<ProjectVO> getActiveProjects() {
        final List<ProjectVO> activeProjects = new ArrayList<>();

        try (final Statement statement = connection.createStatement()) {
            try (final ResultSet rs = statement.executeQuery("select * from project where active = True")) { //$NON-NLS-1$
                while (rs.next()) {
                    ProjectVO project = new ProjectVO(rs.getString("project_id"), rs.getString("title"), rs.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    project.setActive(true);
                    activeProjects.add(project);
                }
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }

        return Collections.unmodifiableList(activeProjects);
    }

    /**
     * Getter for all projects (both active and inactive).
     * @return read-only view of the projects
     */
    public List<ProjectVO> getAllProjects() {
        final List<ProjectVO> allProjects = new ArrayList<>();

        try (final Statement statement = connection.createStatement()) {
            try (final ResultSet resultSet = statement.executeQuery("select * from project")) { //$NON-NLS-1$
                while (resultSet.next()) {
                    final ProjectVO project = new ProjectVO(resultSet.getString("project_id"), resultSet.getString("title"), resultSet.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    project.setActive(resultSet.getBoolean("active")); //$NON-NLS-1$

                    allProjects.add(project);
                }
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }

        return Collections.unmodifiableList(allProjects);
    }

    /**
     * Provides all activities.
     * @return read-only view of the activities
     */
    public List<ActivityVO> getActivities() {
        return getActivities(null);
    }

    /**
     * Adds a new activity.
     * @param activity the activity to add
     */
    public ActivityVO addActivity(final ActivityVO activity) {
        if (activity == null) {
            return null;
        }

        // TODO: Check if exists
        final String sql = "insert into activity (activity_id, description, start, end, project_id) values (?, ?, ?, ?, ?)"; //$NON-NLS-1$
        try (final PreparedStatement preparedStatement = prepare(sql)) {
            String activityId = UUID.randomUUID().toString();
            preparedStatement.setString(1, activityId);
            preparedStatement.setString(2, activity.getDescription());

            final Timestamp d = new Timestamp( activity.getStart().getMillis());
            preparedStatement.setTimestamp(3,d);

            final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
            preparedStatement.setTimestamp(4, endDate);

            preparedStatement.setObject(5, activity.getProject().getId());

            preparedStatement.execute();

            activity.setId(activityId);
            return activity;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Removes an activity.
     * @param activity the activity to remove
     */
    public void removeActivity(final ActivityVO activity) {
        if (activity == null) {
            return;
        }

        try (final PreparedStatement preparedStatement = prepare("delete from activity where activity_id = ?")) { //$NON-NLS-1$
            preparedStatement.setString(1, activity.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Adds a bunch of projects.
     * @param projects the projects to add
     */
    public void addProjects(final Collection<ProjectVO> projects) {
        if (projects == null || projects.size() == 0) {
            return;
        }

        for (ProjectVO project : projects) {
            addProject(project);
        }
    }

    /**
     * Adds a bunch of activities.
     * @param activities the activities to add
     */
    public Collection<ActivityVO> addActivities(final Collection<ActivityVO> activities) {
        if (activities == null || activities.size() == 0) {
            return null;
        }

        for (ActivityVO activity : activities) {
            addActivity(activity);
        }

        return activities;
    }

    /**
     * Removes a bunch of activities.
     * @param activities the activities to remove
     */
    public void removeActivities(final Collection<ActivityVO> activities) {
        if (activities == null || activities.size() == 0) {
            return;
        }

        for (ActivityVO activity : activities) {
            removeActivity(activity);
        }
    }

    /**
     * Updates the project in the database. Pending changes will be made persistent.
     * @param project the project to update
     */
    public void updateProject(final ProjectVO project) {
        if (project == null) {
            return;
        }

        // TODO: Check if exists
        final String sql = "update project set title = ?, description = ?, active = ? where project_id = ?"; //$NON-NLS-1$
        try (final PreparedStatement preparedStatement = prepare(sql)) {
            preparedStatement.setString(1, project.getTitle());
            preparedStatement.setString(2, project.getDescription());
            preparedStatement.setBoolean(3, project.isActive());
            preparedStatement.setString(4, project.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Updates the activity in the database. Pending changes will be made persistent.
     * @param activity the activity to update
     */
    public void updateActivity(final ActivityVO activity) {
        if (activity == null) {
            return;
        }

        // TODO: Check if exists
        final String sql = "update activity set description = ?, start = ?, end = ?, project_id = ? where activity_id = ?"; //$NON-NLS-1$
        try (final PreparedStatement preparedStatement = prepare(sql)) {
            preparedStatement.setString(1, activity.getDescription());

            final Timestamp d = new Timestamp( activity.getStart().getMillis());
            preparedStatement.setTimestamp(2,d);

            final Timestamp endDate = new Timestamp( activity.getEnd().getMillis());
            preparedStatement.setTimestamp(3, endDate);

            preparedStatement.setObject(4, activity.getProject().getId());
            preparedStatement.setString(5, activity.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Find a project by it's id.
     * @param projectId the id of the project
     * @return the project with the given id or <code>null</code> if there is none
     */
    public Optional<ProjectVO> findProjectById(final String projectId) {
        if (projectId == null) {
            return Optional.empty();
        }

        try (final PreparedStatement preparedStatement = prepare("select * from project where project_id = ?")) { //$NON-NLS-1$
            preparedStatement.setString(1, projectId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    final ProjectVO project = new ProjectVO(rs.getString("project_id"), rs.getString("title"), rs.getString("description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    project.setActive(rs.getBoolean("active")); //$NON-NLS-1$
                    return Optional.ofNullable(project);
                }
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }

        return Optional.empty();
    }

    @Override
    public boolean isProjectAdministrationAllowed() {
        return true;
    }

    /**
     * Provides all activities satisfying the given filter.
     * @param filter the filter for activities
     * @return read-only view of the activities
     */
    public List<ActivityVO> getActivities(final FilterVO filter) {
        String sqlCondition = ""; //$NON-NLS-1$

        if (filter != null && filter.getTimeInterval() != null) {
            sqlCondition += " and ? <= activity.start and activity.start < ?"; //$NON-NLS-1$
        }

        final List<ActivityVO> activities = new ArrayList<>();
        final String filterCondition = StringUtils.defaultString(sqlCondition);
        final String sql = "select * from activity, project where activity.project_id = project.project_id " + filterCondition + " order by start asc"; //$NON-NLS-1$ //$NON-NLS-2$

        try (final PreparedStatement preparedStatement = prepare(sql)) {
            if (filter != null && filter.getTimeInterval() != null) {
                preparedStatement.setDate(1, new java.sql.Date(filter.getTimeInterval().getStart().toDate().getTime()));
                preparedStatement.setDate(2, new java.sql.Date(filter.getTimeInterval().getEnd().toDate().getTime()));
            }

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    final ProjectVO project = new ProjectVO(resultSet.getString("project.project_id"), resultSet.getString("title"), resultSet.getString("project.description")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    project.setActive(resultSet.getBoolean("active")); //$NON-NLS-1$

                    final ActivityVO activity = new ActivityVO(new DateTime(resultSet.getTimestamp("start")), new DateTime(resultSet.getTimestamp("end")), project); //$NON-NLS-1$ //$NON-NLS-2$
                    activity.setId(resultSet.getString("activity.activity_id")); //$NON-NLS-1$
                    activity.setDescription(resultSet.getString("activity.description")); //$NON-NLS-1$

                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }

        return activities;
    }

    /**
     * Gathers some statistics about the tracked activities.
     */
    public void gatherStatistics() {
        try (final Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select count(*) as rowcount from activity")) { //$NON-NLS-1$
                if (resultSet.next()) {
                    log.error("#activities: " + resultSet.getInt("rowcount")); //$NON-NLS-1$
                }
            }

            try (ResultSet resultSet = statement.executeQuery("select count(*) as rowcount from project")) { //$NON-NLS-1$
                if (resultSet.next()) {
                    log.error("#projects: " + resultSet.getInt("rowcount")); //$NON-NLS-1$
                }
            }

            java.util.Date earliestDate = null;
            try (ResultSet resultSet = statement.executeQuery("select min(start) as startDate from activity")) { //$NON-NLS-1$
                if (resultSet.next()) {
                    earliestDate = resultSet.getDate("startDate");
                    log.error("earliest activity: " + earliestDate); //$NON-NLS-1$
                }
            }

            java.util.Date latestDate = null;
            try (ResultSet resultSet = statement.executeQuery("select max(start) as startDate from activity")) { //$NON-NLS-1$
                if (resultSet.next()) {
                    latestDate = resultSet.getDate("startDate");
                    log.error("latest activity: " + latestDate); //$NON-NLS-1$
                }
            }

            if (earliestDate != null && latestDate != null) {
                Duration dur = new Duration(earliestDate.getTime(), latestDate.getTime());
                log.error("using for " + PeriodFormat.getDefault().print(dur.toPeriod())); //$NON-NLS-1$

            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

    /**
     * Removes all projects and activities from the database.
     */
    public void clearData() {
        try (final PreparedStatement activitiesDelete = prepare("delete from activity")) { //$NON-NLS-1$
            // Remove activities
            activitiesDelete.execute();

            // Remove the projects
            try (final PreparedStatement projectsDelete = prepare("delete from project")) { //$NON-NLS-1$
                projectsDelete.execute();
            }
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(textBundle.textFor("BaralgaDB.DatabaseError.Message"), e); //$NON-NLS-1$
        }
    }

}
