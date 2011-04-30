package org.remast.baralga.model.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import org.remast.baralga.gui.settings.ApplicationSettings;





public class H2 extends AbstractDatabaseInstance
{

    
    public H2(DatabaseConfig config)
    {
        super(config);
    }
    
    
    @Override
    public void createConnection() throws SQLException
    {
        // FIXME
        if (this.m_connection==null)
        {
            final String dataDirName = ApplicationSettings.instance().getApplicationDataDirectory().getAbsolutePath();
            this.m_connection = DriverManager.getConnection("jdbc:h2:" + dataDirName + "/baralga;DB_CLOSE_ON_EXIT=FALSE", "baralga-user", ""); 
        }
    }

   
    
    /** Statement to insert the current database version. */
//    private static final String versionTableInsert = "insert into db_version (version, description) values (1, 'Initial database setup.')"; //$NON-NLS-1$

    /** Statement to create table for the projects. */
/*    private static final String projectTableCreate =  
        "create table project (" + //$NON-NLS-1$
        "     id           identity," + //$NON-NLS-1$
        "     title        varchar(255)," + //$NON-NLS-1$
        "     description  varchar(4000)," + //$NON-NLS-1$
        "     active       boolean" + //$NON-NLS-1$
        "    )"; //$NON-NLS-1$
*/
    /** Statement to create table for the activities. */
  /*
    private static final String activityTableCreate =  
        "create table activity (" + //$NON-NLS-1$
        "     id           identity," + //$NON-NLS-1$
        "     description  varchar(4000)," + //$NON-NLS-1$
        "     start        timestamp," + //$NON-NLS-1$
        "     end          timestamp," + //$NON-NLS-1$
        "     project_id   number," + //$NON-NLS-1$
        "     FOREIGN key (project_id) REFERENCES project(id)" + //$NON-NLS-1$
        "    )"; //$NON-NLS-1$
*/
    
    @Override
    public void disposeDbSpecial()
    {
    }
    
    
    public String getJDBCDriverName()
    {
        // FIXME
        return "";
    }

    
    public boolean checkIfDbTablesExists()
    {
        try
        {
        
            Statement statement = this.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SHOW TABLES"); //$NON-NLS-1$
            while (resultSet.next()) 
            {
                if (this.m_config.getTableName(AbstractDatabaseInstance.TABLE_DB_VERSION).equalsIgnoreCase(resultSet.getString("TABLE_NAME"))) 
                { 
                    return true; 
                }
            }
            
            return false;
        }
        catch(Exception ex)
        {
            return false;
        }
        
    }
    

    public void initSQLStatements()
    {
        
        this.sql_statements = new Hashtable<String,String>();

        this.sql_statements.put("Create." + AbstractDatabaseInstance.TABLE_DB_VERSION,
            "create table " + m_config.getTableName(AbstractDatabaseInstance.TABLE_DB_VERSION) + " (" + 
            "     id           identity," + 
            "     version      number," + 
            "     created_at   timestamp," + 
            "     description  varchar2(255)" + 
            "    )"); 

        this.sql_statements.put("Create." + AbstractDatabaseInstance.TABLE_PROJECTS,
            "create table " + m_config.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS) + " (" + 
            "     id           identity," + 
            "     title        varchar(255)," + 
            "     description  varchar(4000)," + 
            "     active       boolean" + 
            "    )"); 

        this.sql_statements.put("Create." + AbstractDatabaseInstance.TABLE_ACTIVITY,
            "create table " + m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " (" + 
            "     id           identity," + 
            "     description  varchar(4000)," + 
            "     start        timestamp," + 
            "     end          timestamp," + 
            "     project_id   number," + 
            "     FOREIGN key (project_id) REFERENCES " + m_config.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS) + "(id)" + 
            "    )"); 
        
        this.sql_statements.put("Insert.DbVersion.Initial",
            "insert into db_version (version, description) values (1, 'Initial database setup.')");
        
        // FIXME: NOT TESTED
        // Type: SQL Query 
        // Returns: activity_id, activity_description, activity_start, activity_end, project_id, project_title, project_description
        this.sql_statements.put("GetActivities", 
            "select activity.id as activity_id, activity.description as activity_description, activity.start as activity_start, " +
            "activity.end as activity_end, activity.project_id as project_id, " +
            "project.title as project_title, project.description as project_description, " +
            "project.active as project_active " +
            "from  " + this.m_config.getTableName(TABLE_ACTIVITY) + " as activity " +
            "INNER JOIN " + this.m_config.getTableName(TABLE_PROJECTS) + " as project ON activity.project_id = project.id " +  
            "order by start asc ");
     
        this.sql_statements.put("ActivitiesFilter.Year", 
            " and year(activity.start) = %PARAM% ");
        
        this.sql_statements.put("ActivitiesFilter.Month", 
        " and month(activity.start) = %PARAM% ");
        
        this.sql_statements.put("ActivitiesFilter.Week", 
        " and week(activity.start) = %PARAM% ");
        
        this.sql_statements.put("ActivitiesFilter.DOW", 
        " and day_of_week(activity.start) = %PARAM% ");
        
        this.sql_statements.put("Insert.Activity",
            "insert into " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " (description, start, end, project_id) values (?, ?, ?, ?) ");

        this.sql_statements.put("Update.Activity",
            "update " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " set description = ?, start = ?, end = ?, project_id = ? where id = ?");
        
        this.sql_statements.put("Insert.Project",
            "insert into " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_PROJECTS) + " (title, description, active) values (?, ?, ?) ");
        
        this.sql_statements.put("GetDistinctMonths",
            "select distinct month(start) as month from " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " order by month desc ");

        this.sql_statements.put("GetDistinctYears",
            "select distinct year(start) as year from " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " order by year desc ");
        
        this.sql_statements.put("GetDistinctWeeks",
            "select distinct week(start) as week from " + this.m_config.getTableName(AbstractDatabaseInstance.TABLE_ACTIVITY) + " order by week desc ");
        

        
        
         System.out.println("INIT SQL STATEMENTS: H2");
        
        
    }
    
    
    public int convertFromJodaDay(int yoda_day)
    {
        // :TRICKY: Day of the week in joda time and h2 database differ by one. Therfore
        // we have to add one and make sure that it never succeeds 7.
        
        return (yoda_day % 7) + 1;
    }
    
    
}
