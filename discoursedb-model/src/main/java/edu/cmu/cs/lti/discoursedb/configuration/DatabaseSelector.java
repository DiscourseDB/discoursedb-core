package edu.cmu.cs.lti.discoursedb.configuration;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Component
public class DatabaseSelector extends AbstractRoutingDataSource {
	@Autowired 
	private Environment environment;

	private static final ThreadLocal<String> currentDatabase = new ThreadLocal<String>();
	private java.util.Map<java.lang.Object,java.lang.Object> myTargetDataSources = new HashMap<java.lang.Object,java.lang.Object>();
	
	@Override
	protected Object determineCurrentLookupKey() {
		return currentDatabase.get();
	}	
	
	@PostConstruct
	public void init() {
		setTargetDataSources(myTargetDataSources);
		setDataSourceLookup((dsname) -> (DataSource)myTargetDataSources.get(dsname));
		String defaultdb = environment.getRequiredProperty("jdbc.database");
		myTargetDataSources.put(defaultdb, getSpecificDataSource(defaultdb));
		setDefaultTargetDataSource(defaultdb);
		afterPropertiesSet();
	}
	
	public void changeDatabase(String dbName) {
		System.out.println("CURRENT DATABASE CHANGE ===============> " + dbName);
		if (!myTargetDataSources.containsKey(dbName)) {
			myTargetDataSources.put(dbName,  getSpecificDataSource(dbName));
		}
		currentDatabase.set(dbName);
		afterPropertiesSet();
		System.out.println("Changed db to ~~~~>" + currentDatabase.get());
	}

	private DataSource getSpecificDataSource(String dbname) {
		try {
			System.out.println("CONNECTING TO DB ---------------> " + dbname);

			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
			String host = environment.getRequiredProperty("jdbc.host");
			String port = environment.getRequiredProperty("jdbc.port");
			String database = dbname;
			ds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database+ "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false");
			ds.setUser(environment.getRequiredProperty("jdbc.username"));
			ds.setPassword(environment.getRequiredProperty("jdbc.password"));
			ds.setAcquireIncrement(Integer.parseInt(environment.getRequiredProperty("c3p0.acquireIncrement").trim()));
			ds.setIdleConnectionTestPeriod(
					Integer.parseInt(environment.getRequiredProperty("c3p0.idleConnectionTestPeriod").trim()));
			ds.setMaxStatements(Integer.parseInt(environment.getRequiredProperty("c3p0.maxStatements").trim()));
			ds.setMinPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.minPoolSize").trim()));
			ds.setMaxPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.maxPoolSize").trim()));
			return ds;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
