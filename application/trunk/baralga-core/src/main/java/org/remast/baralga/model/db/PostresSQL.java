package org.remast.baralga.model.db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PostresSQL extends AbstractDatabaseInstance {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(PostresSQL.class);

	public PostresSQL(DatabaseConfig config) {
		super(config);
		db_jdbc_driver_name = "org.postgresql.Driver";
	}

	public static final String db_name = "Db_PgSQL";

	public void initSQLStatements() {

		this.sql_statements = new Hashtable<String, String>();

		this.sql_statements.put("CreateTable."
				+ AbstractDatabaseInstance.TABLE_DB_VERSION, "create table "
				+ m_config.getTableName(AbstractDatabaseInstance.TABLE_DB_VERSION)
				+ " (" + "     id           id bigserial NOT NULL,"
				+ "     \"version\" integer,"
				+ "     created_at timestamp without time zone,"
				+ "     description character varying(255) "
				+ "    ) WITH ( OIDS=FALSE );   "
				+ " ALTER TABLE brg_db_version OWNER TO mojsp; ");

		this.sql_statements
				.put("CreateTable." + AbstractDatabaseInstance.TABLE_PROJECTS,
						"create table "
								+ m_config
										.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS)
								+ " (" + "     id           identity,"
								+ "     title        varchar(255),"
								+ "     description  varchar(4000),"
								+ "     active       boolean" + "    )");

		this.sql_statements
				.put("CreateTable." + AbstractDatabaseInstance.TABLE_ACTIVITY,
						"create table "
								+ m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " ("
								+ "     id           identity,"
								+ "     description  varchar(4000),"
								+ "     start        timestamp,"
								+ "     end          timestamp,"
								+ "     project_id   number,"
								+ "     FOREIGN key (project_id) REFERENCES "
								+ m_config
										.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS)
								+ "(id)" + "    )");

		// SQL Query
		// Returns: activity_id, activity_description, activity_start,
		// activity_end, project_id, project_title, project_description
		this.sql_statements
				.put("GetActivities",
						"select activity.id as activity_id, activity.description as activity_description, activity.start as activity_start, "
								+ "activity.end as activity_end, activity.project_id as project_id, "
								+ "project.title as project_title, project.description as project_description, "
								+ "project.active as project_active "
								+ "from  "
								+ this.m_config.getTableName(TABLE_ACTIVITY)
								+ " as activity "
								+ "INNER JOIN "
								+ this.m_config.getTableName(TABLE_PROJECTS)
								+ " as project ON activity.project_id = project.id "
								+ " WHERE 1=1  %FILTER%  "
								+ "order by start asc ");

		this.sql_statements.put("ActivitiesFilter.Year",
				" and extract(year from activity.start) = %PARAM% ");

		this.sql_statements.put("ActivitiesFilter.Month",
				" and extract(month from activity.start) = %PARAM% ");

		this.sql_statements.put("ActivitiesFilter.Week",
				" and extract(week from activity.start) = %PARAM% ");

		this.sql_statements.put("ActivitiesFilter.DOW",
				" and extract(dow from activity.start) = %PARAM% ");

		this.sql_statements
				.put("Insert.Activity",
						"insert into "
								+ this.m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " (description, \"start\", \"end\", project_id) values (?, ?, ?, ?) ");

		this.sql_statements
				.put("Update.Activity",
						"update "
								+ this.m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " set description = ?, \"start\" = ?, \"end\" = ?, project_id = ? where id = ?");

		this.sql_statements.put("Insert.Project", "insert into "
				+ this.m_config.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS)
				+ " (title, description, active) values (?, ?, ?) ");

		this.sql_statements
				.put("GetDistinctMonths",
						"select distinct extract(month from start) as month from "
								+ this.m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " order by month desc");

		this.sql_statements
				.put("GetDistinctYears",
						"select distinct extract(year from start) as year from "
								+ this.m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " order by year desc ");

		this.sql_statements
				.put("GetDistinctWeeks",
						"select distinct extract(week from start) as week from "
								+ this.m_config
										.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY)
								+ " order by week desc ");

		System.out.println("INIT SQL STATEMENTS: PgSQL");

	}

	public int convertFromJodaDay(int yoda_day) {
		if (yoda_day == 7)
			return 0;
		else
			return yoda_day;
	}

	@Override
	public void disposeDbSpecial() {
	}

	public boolean checkIfDbTablesExists() {
		try {

			Statement statement = this.getConnection().createStatement();
			ResultSet resultSet = statement
					.executeQuery("select table_name from information_schema.tables where table_schema='public' and table_type='BASE TABLE'"); //$NON-NLS-1$
			while (resultSet.next()) {
				if (this.m_config.getTableName(
						AbstractDatabaseInstance.TABLE_DB_VERSION).equalsIgnoreCase(
						resultSet.getString("table_name"))) {
					return true;
				}
			}

			return false;
		} catch (Exception ex) {
			return false;
		}

	}

}
