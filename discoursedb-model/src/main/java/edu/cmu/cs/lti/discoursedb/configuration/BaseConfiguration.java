package edu.cmu.cs.lti.discoursedb.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DiscourseDB base configuration class.
 * Parameters that are most likely to be changed (i.e. for the databse connection) are read from the hibernate.properties file.
 * 
 * Fore more information about the Spring JavaConfig, see <a href="http://docs.spring.io/spring-data/jpa/docs/1.4.3.RELEASE/reference/html/jpa.repositories.html">the Spring Data docs</a>.
 * <br/>
 * The configuration class be replaced by a custom configuration.
 */
@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.core.model","edu.cmu.cs.lti.discoursedb.core.repository","edu.cmu.cs.lti.discoursedb.core.service"})
@PropertySource(value = { "classpath:hibernate.properties" })
@EntityScan(basePackages = { "edu.cmu.cs.lti.discoursedb.core.model" })
@EnableJpaRepositories(basePackages = { "edu.cmu.cs.lti.discoursedb.core.repository" })
public class BaseConfiguration {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() {
		try {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
			ds.setJdbcUrl(environment.getRequiredProperty("jdbc.url"));
			ds.setUser(environment.getRequiredProperty("jdbc.username"));
			ds.setPassword(environment.getRequiredProperty("jdbc.password"));
			ds.setAcquireIncrement(Integer.parseInt(environment.getRequiredProperty("c3p0.acquireIncrement").trim()));
			ds.setIdleConnectionTestPeriod(Integer.parseInt(environment.getRequiredProperty("c3p0.idleConnectionTestPeriod").trim()));
			ds.setMaxStatements(Integer.parseInt(environment.getRequiredProperty("c3p0.maxStatements").trim()));
			ds.setMinPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.minPoolSize").trim()));
			ds.setMaxPoolSize(Integer.parseInt(environment.getRequiredProperty("c3p0.maxPoolSize").trim()));
			return ds;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	@Bean
	EntityManagerFactory entityManagerFactory(DataSource dataSource, Environment env) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	    vendorAdapter.setGenerateDdl(true);
		    
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
	    factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("edu.cmu.cs.lti.discoursedb");

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
		jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
		jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
		jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
		jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
		jpaProperties.put("hibernate.jdbc.batch_size", env.getRequiredProperty("hibernate.jdbc.batch_size"));		
		factory.setJpaProperties(jpaProperties);
		factory.afterPropertiesSet();

		return factory.getObject();
	}

	@Bean
	JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

}