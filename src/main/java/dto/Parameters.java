package dto;

public class Parameters {
	
	private String dbUserName;
	private String dbPassword;
	private String jdbcUrl;
	private String driverClass;
	private String initialPath;
	
	public Parameters(){
		
	}
	
	public Parameters(String parameters[]){
		if(parameters!=null){
			dbUserName=parameters[0];
			dbPassword=parameters[1];
			jdbcUrl=parameters[2];
			driverClass=parameters[3];
			initialPath=parameters[4];
		}
	}
	 
	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getInitialPath() {
		return initialPath;
	}

	public void setInitialPath(String initialPath) {
		this.initialPath = initialPath;
	}
	 
	 

}
