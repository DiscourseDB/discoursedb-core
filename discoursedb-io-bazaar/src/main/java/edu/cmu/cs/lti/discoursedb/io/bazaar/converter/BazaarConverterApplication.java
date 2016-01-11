package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.bazaar"})
public class BazaarConverterApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(BazaarConverterApplication.class, args);
		
	}

}
