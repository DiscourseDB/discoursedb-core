package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger logger = LogManager.getLogger(BazaarConverterApplication.class);
	
	public static void main(String[] args) {		
		if (args.length != 4) {
			logger.error("Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log>");
			return;
		}
		SpringApplication.run(BazaarConverterApplication.class, args);		
	}

}
