package edu.cmu.cs.lti.discoursedb.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.service.spi.ServiceRegistryAwareService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import edu.cmu.cs.lti.discoursedb.configuration.Utilities;

@Component
@Primary
@ConfigurationProperties(prefix="core.datasource")
public class DatabaseSelector extends AbstractRoutingDataSource  {
	
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
		String defaultdb = environment.getProperty("jdbc.database", "dummy");
		myTargetDataSources.put(defaultdb, getSpecificDataSource(defaultdb));
		setDefaultTargetDataSource(defaultdb);
		afterPropertiesSet();
		if (environment.getProperty("discoursedb.force.authentication", "false").equals("false")) {
			Utilities.becomeSuperUser();
		}
	}
	
	public Set<Object> listOpenDatabases() { return myTargetDataSources.keySet(); }
	
	public void changeDatabase(String dbName) {
		System.out.println("CURRENT DATABASE CHANGE ===============> " + dbName);
		if (!myTargetDataSources.containsKey(dbName)) {
			myTargetDataSources.put(dbName,  getSpecificDataSource(dbName));
		}
		currentDatabase.set(dbName);
		afterPropertiesSet();
		System.out.println("Changed db to ~~~~>" + currentDatabase.get());
	}

	@ConfigurationProperties(prefix="system.datasource")
	private DataSource getSpecificDataSource(String dbname) {
		try {
			System.out.println("CONNECTING TO DB ---------------> " + dbname.replaceAll("discoursedb_ext_", ""));

			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
			String host = environment.getRequiredProperty("jdbc.host");
			String port = environment.getRequiredProperty("jdbc.port");
			String database = "discoursedb_ext_" + dbname.replaceAll("discoursedb_ext_", "");
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
