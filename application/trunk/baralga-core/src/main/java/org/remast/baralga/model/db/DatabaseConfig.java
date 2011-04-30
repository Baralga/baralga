package org.remast.baralga.model.db;


public class DatabaseConfig {
	protected String table_names[] = { "db_version", "project", "activity" };
	protected AbstractDatabaseInstance db_instance = null;

	protected String db_jdbc_url = null;
	protected String db_username = null;
	protected String db_password = null;

	public DatabaseConfig() {
		// load settings
		initSpecial();
	}

	public void initSpecial() {
		// read config
		// create instance
	}

	public AbstractDatabaseInstance getSelectedDatabaseInstance() {
		return this.db_instance;
	}

	public String getTableName(int table_type) {
		return table_names[table_type];
	}

	public String getDbUser() {
		return this.db_username;
	}

	public String getDbPassword() {
		return this.db_password;
	}

	public String getDbJDBC_URL() {
		return this.db_jdbc_url;
	}

}
