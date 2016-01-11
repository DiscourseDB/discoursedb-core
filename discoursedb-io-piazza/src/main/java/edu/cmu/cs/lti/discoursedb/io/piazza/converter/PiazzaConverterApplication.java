package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author Oliver Ferschke
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.piazza.converter"})
public class PiazzaConverterApplication {

	private static final Logger logger = LogManager.getLogger(PiazzaConverterApplication.class);
	
	public static void main(String[] args) {		
		SpringApplication.run(PiazzaConverterApplication.class, args);		
	}

}
