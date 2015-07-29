package edu.cmu.cs.lti.discoursedb.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "edu.cmu.cs.lti.discoursedb")
public class DiscourseDBConfig {

	/*
	 * Currently, this class is empty and only reason for it’s existence is @ComponentScan 
	 * which provides beans auto-detection facility. 
	 * We may completely remove above configuration and put the component scan logic in 
	 * application context level (in Main ). 
	 * Later, we may find it handy to configure some beans 
	 * (e.g. messageSource, PropertySourcesPlaceHolderConfigurer) in Configuration class.
	 */

}
