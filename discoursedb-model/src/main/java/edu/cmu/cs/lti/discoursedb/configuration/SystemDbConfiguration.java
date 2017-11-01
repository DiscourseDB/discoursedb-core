/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DiscourseDB system configuration class.
 * This configures a database of system users (i.e. researchers examining this data) and their
 * permissions.
 * The database(s) of actual discourse data are configured in BaseConfiguration 
 * 
 * https://scattercode.co.uk/2016/01/05/multiple-databases-with-spring-boot-and-spring-data-jpa/
 * 
 * Parameters that are most likely to be changed (i.e. for the databse connection) are read from the hibernate.properties file.
 * 
 * Fore more information about the Spring JavaConfig, see <a href="http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/jpa.repositories.html">the Spring Data docs</a>.
 * <br/>
 */
@Configuration
//@EnableAutoConfiguration
@EnableTransactionManagement
/*@ComponentScan(basePackages = { 
		"edu.cmu.cs.lti.discoursedb.system.model",
		"edu.cmu.cs.lti.discoursedb.system.repository",
		"edu.cmu.cs.lti.discoursedb.system.service"
})*/
@PropertySources({
    @PropertySource("classpath:hibernate.properties"), //default hibernate configuration
    @PropertySource("classpath:jdbc.properties"), //default database configuration
    @PropertySource("classpath:c3p0.properties"), //default connection pool configuration
    @PropertySource(value = "classpath:custom.properties", ignoreResourceNotFound = true) //optional custom config. keys specified here override defaults 
})
//@EntityScan(basePackages = { "edu.cmu.cs.lti.discoursedb.system.model" })
/*
 *  May need to define entityManagerFactoryRef and transactionManagerRef below, per
 *  
 * 			http://kimrudolph.de/blog/spring-datasource-routing
 */
@EnableJpaRepositories(basePackages = { "edu.cmu.cs.lti.discoursedb.system.repository" },
					  entityManagerFactoryRef = "systemEntityManagerFactory",
					  transactionManagerRef = "systemTransactionManager")
public class SystemDbConfiguration {

	@Autowired 
	private Environment environment;
	
	//@Autowired(required = false)
	//private PersistenceUnitManager systemPersistenceUnitManager;
	@Bean(name="systemPersistenceUnitManager")
	public PersistenceUnitManager systemPersistenceUnitManager() {
		DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
		persistenceUnitManager.setDefaultDataSource(this.systemDataSource());
		persistenceUnitManager.setPackagesToScan("edu.cmu.cs.lti.discoursedb.system.model");
		persistenceUnitManager.setDefaultPersistenceUnitName("systemPersistenceUnitManager");
		return persistenceUnitManager;
	}
	
	/*@Bean(name="systemEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean systemDiscoursedbEntityManager(
			final Properties customerJpaProperties,
			@Qualifier("systemDataSource") DataSource systemDataSource) {
		EntityManagerFactoryBuilder builder =
				createEntityManagerFactoryBuilder(customerJpaProperties);
		return builder.dataSource(systemDataSource).packages("edu.cmu.cs.lti.discoursedb.system.model")
				.persistenceUnit("systemDiscoursedbEntityManager").build();
	}*/
	@Bean(name="systemEntityManagerFactory")
	LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
			 Environment env) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(this.systemDataSource());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPersistenceUnitManager(this.systemPersistenceUnitManager());
		factory.setPackagesToScan("edu.cmu.cs.lti.discoursedb.core.system");
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.connection.useUnicode", true);
		jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
		jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
		jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		jpaProperties.put("hibernate.jdbc.batch_size", env.getRequiredProperty("hibernate.jdbc.batch_size"));
		jpaProperties.put("hibernate.order_inserts", true);
		jpaProperties.put("hibernate.order_updates", true);
		jpaProperties.put("hibernate.id.new_generator_mappings", Boolean.parseBoolean(environment.getRequiredProperty("hibernate.id.new_generator_mappings").trim()));
		factory.setJpaProperties(jpaProperties);

		return factory;
	}
	
	@Bean(name="systemDiscoursedbTransactionManager")
	public JpaTransactionManager systemDiscoursedbTransactionManager(
			@Qualifier("systemEntityManagerFactory") final EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}
	
	

	/*
	private EntityManagerFactoryBuilder createEntityManagerFactoryBuilder(
			JpaProperties discoursedbJpaProperties) {
		JpaVendorAdapter jpaVendorAdapter = 
				createJpaVendorAdapter(discoursedbJpaProperties);
		return new EntityManagerFactoryBuilder(jpaVendorAdapter,
				discoursedbJpaProperties.getProperties(), this.systemPersistenceUnitManager);
	}
	
	private JpaVendorAdapter createJpaVendorAdapter(
		    JpaProperties jpaProperties) {
		    AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		    adapter.setShowSql(jpaProperties.isShowSql());
		    adapter.setDatabase(jpaProperties.getDatabase());
		    adapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
		    adapter.setGenerateDdl(jpaProperties.isGenerateDdl());
		    return adapter;
	}*/
	
	
	
	@Bean(name = "systemDataSource")
    @ConfigurationProperties(prefix="system.datasource")
   public DataSource systemDataSource() {
		try {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
			String host = environment.getRequiredProperty("jdbc.host");
			String port = environment.getRequiredProperty("jdbc.port");
			String database = environment.getRequiredProperty("jdbc.system_database").replaceAll("discoursedb_ext", "");
			ds.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/discoursedb_ext_" + database+ "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false");
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

	


	@Bean(name="systemTransactionManager")
	PlatformTransactionManager systemTransactionManager(
			@Qualifier("systemEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

}