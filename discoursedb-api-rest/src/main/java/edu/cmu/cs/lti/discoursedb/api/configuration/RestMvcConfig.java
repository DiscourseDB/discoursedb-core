package edu.cmu.cs.lti.discoursedb.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;


/*
 * This is necessary to turn of some unwanted REST endpoints ("/profile/*")
 */
@Configuration
public class RestMvcConfig extends RepositoryRestMvcConfiguration {
	@Override
    protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.getMetadataConfiguration().setAlpsEnabled(false);
    }
	
}