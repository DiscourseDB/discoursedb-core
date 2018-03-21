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
package edu.cmu.cs.lti.discoursedb.core.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DiscourseDB base configuration class.
 * Parameters that are most likely to be changed (i.e. for the databse connection) are read from the hibernate.properties file.
 * 
 * Fore more information about the Spring JavaConfig, see <a href="http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/jpa.repositories.html">the Spring Data docs</a>.
 * <br/>
 * The configuration class be replaced by a custom configuration.
 */
@Configuration
//@EnableAutoConfiguration
@EnableTransactionManagement
/*@ComponentScan(basePackages = { 
		"edu.cmu.cs.lti.discoursedb.core.model",
		"edu.cmu.cs.lti.discoursedb.core.repository",
		"edu.cmu.cs.lti.discoursedb.core.service"
})*/
@PropertySources({
    @PropertySource("classpath:hibernate.properties"), //default hibernate configuration
    @PropertySource("classpath:jdbc.properties"), //default database configuration
    @PropertySource("classpath:c3p0.properties"), //default connection pool configuration
    @PropertySource(value = "classpath:custom.properties", ignoreResourceNotFound = true) //optional custom config. keys specified here override defaults 
})
//@EntityScan(basePackages = { "edu.cmu.cs.lti.discoursedb.core.model" })
/*
 *  May need to define entityManagerFactoryRef and transactionManagerRef below, per
 *  
 * 			http://kimrudolph.de/blog/spring-datasource-routing
 */
@ConfigurationProperties(prefix="core.datasource")
@EnableJpaRepositories(basePackages = { "edu.cmu.cs.lti.discoursedb.core.repository" },
					  entityManagerFactoryRef = "coreEntityManagerFactory",
					  transactionManagerRef = "coreTransactionManager")
public class BaseConfiguration  {

	@Autowired 
	private Environment environment;
	
	//@Autowired(required = false)
	@Bean(name="corePersistenceUnitManager")
	@Primary
	public PersistenceUnitManager corePersistenceUnitManager(DatabaseSelector dataSource) {
		DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
		persistenceUnitManager.setDefaultDataSource(dataSource);
		persistenceUnitManager.setPackagesToScan("edu.cmu.cs.lti.discoursedb.core.model");
		persistenceUnitManager.setDefaultPersistenceUnitName("corePersistenceUnitManager");
		return persistenceUnitManager;
	}
	
	

	@Bean(name="coreTransactionManager")
	@Primary
	public JpaTransactionManager discoursedbTransactionManager(
			@Qualifier("coreEntityManagerFactory") final EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}



	@Bean(name="coreEntityManagerFactory")
	@Primary
	LocalContainerEntityManagerFactoryBean coreEntityManagerFactory(
			 DatabaseSelector dataSource, Environment env) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPersistenceUnitManager(this.corePersistenceUnitManager(dataSource));
		factory.setPackagesToScan("edu.cmu.cs.lti.discoursedb.core.model");
		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.connection.useUnicode", true);
		jpaProperties.put("hibernate.connection.characterEncoding", "UTF-8");
		jpaProperties.put("hibernate.enable_lazy_load_no_trans", true);
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



	




}