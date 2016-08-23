package edu.cmu.cs.lti.discoursedb.github.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot starter class which launches all components that can be found in any sub-package of <code>edu.cmu.cs.lti.discoursedb.github.converter</code><br/>
 * The args of the main methods will be passed on to all components that implement the CommandLineRunner interface.<br/>
 * 
 * Not much else is needed in this class. If any parameters are required, it would be good practice to validate them here.
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.github.converter"})
public class GithubConverterApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(GithubConverterApplication.class, args);       
	}
}
