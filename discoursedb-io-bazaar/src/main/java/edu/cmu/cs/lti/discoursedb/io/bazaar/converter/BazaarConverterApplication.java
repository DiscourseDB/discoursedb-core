package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


/**
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.bazaar"})
public class BazaarConverterApplication {

	/**
	 * @param args <Dataset Name> <Discourse name> <chat message log> <chat room log> <agent name>\n A common value for the agent name (args[4]) is VirtualCarolyn.
	 */
	public static void main(String[] args) {		
		Assert.isTrue(args.length==5,"Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log> <agent name>\n A common value for the agent name is VirtualCarolyn.");
		SpringApplication.run(BazaarConverterApplication.class, args);		
	}

}
