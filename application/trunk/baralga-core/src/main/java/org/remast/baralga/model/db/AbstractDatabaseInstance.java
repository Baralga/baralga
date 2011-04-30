package org.remast.baralga.model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDatabaseInstance {
	protected DatabaseConfig m_config;

	// protected String create_sql_statments[] = null;
	protected Connection m_connection = null;

	public static final int TABLE_DB_VERSION = 0;
	public static final int TABLE_PROJECTS = 1;
	public static final int TABLE_ACTIVITY = 2;

	protected String db_jdbc_driver_name = null;
	private static Log log = LogFactory.getLog(AbstractDatabaseInstance.class);

	protected Hashtable<String, String> sql_statements = null;

	public AbstractDatabaseInstance(DatabaseConfig config) {
		m_config = config;
		initSQLStatements();
	}

	public Connection getConnection() throws SQLException {
		if ((this.m_connection == null) || (this.m_connection.isClosed()))
			this.createConnection();

		return this.m_connection;
	}

	public void createConnection() throws SQLException {
		try {
			// Load the database driver
			Class.forName(this.getJDBCDriverName());

			// DriverManager.registerDriver("org.postgresql.Driver");
			// DriverManager..registerDriver(new
			// Driver(this.m_config.getJDBCDriver()));

			// Get a connection to the database
			this.m_connection = DriverManager.getConnection(
					this.m_config.getDbJDBC_URL(), this.m_config.getDbUser(),
					this.m_config.getDbPassword());

			// Print all warnings
			for (SQLWarning warn = this.m_connection.getWarnings(); warn != null; warn = warn
					.getNextWarning()) {
				System.out.println("SQL Warning:");
				System.out.println("State  : " + warn.getSQLState());
				System.out.println("Message: " + warn.getMessage());
				System.out.println("Error  : " + warn.getErrorCode());
			}
		} catch (SQLException ex) {
			log.error("SqlException: " + ex, ex);
		} catch (Exception ex) {
			log.error("Exception: " + ex, ex);
		}

	}

	public String getCreateStatement(int table_type) throws Exception {
		if (this.sql_statements == null)
			throw new Exception("No create sql statements available.");

		if (!this.sql_statements.containsKey("CreateTable." + table_type))
			throw new Exception(
					"Class not fully implemented. Missing create sql statements.");

		return this.sql_statements.get("CreateTable." + table_type);
	}

	public String getJDBCDriverName() {
		return this.db_jdbc_driver_name;
	}

	public void disposeDb() {
		this.disposeDbSpecial();
	}

	public abstract void disposeDbSpecial();

	public abstract boolean checkIfDbTablesExists();

	public abstract void initSQLStatements();

	public String getSQLStatement(String keyword) {
		if ((this.sql_statements == null)
				|| (!this.sql_statements.containsKey(keyword)))
			return null;

		return this.sql_statements.get(keyword);

	}

	public abstract int convertFromJodaDay(int yoda_day);

}
