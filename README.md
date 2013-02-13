jpa-mapping-db-diff (JMDD)
==========================

Compares JPA O-R Mapping and DB model. Indicates differences between both.

HOW JMDD WORKS? 
	
	JMDD reads all JPA Entity mappings, then it reads database model and compares both.
	It indicates the differences between O-R mapping and DB Model.

HOW DO I BUILD JMDD?
	
	mvn install
	
HOW DO I CONFIGURE IT?

	In your project add the jpa-mapping-db-diff dependency in your pom.xml (Or add the jar in your classpath):
	
	<dependency>
		<groupId>jpa-mapping-db-diff</groupId>
		<artifactId>jpa-mapping-db-diff</artifactId>
		<version>0.0.1-SNAPSHOT</version>
		<scope>provided</scope>
	</dependency>

HOW DO I RUN AHC?

	java main.MappingDBChecker [dbUserName] [dbPassword] [jdbcUrl] [driverClass] [entitiesAbsolutePath]
	
	E.G: java main.MappingDBChecker app app jdbc:oracle:thin:@192.168.1.2:1521:sid oracle.jdbc.driver.OracleDriver C:\user\testemap_parent\testemap_commons\src\main\java
	
