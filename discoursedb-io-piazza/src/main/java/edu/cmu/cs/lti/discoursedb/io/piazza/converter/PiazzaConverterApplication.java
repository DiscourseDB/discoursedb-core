package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;


/**
 * This starter class launches the components necessary for importing piazza discussion data
 * from several json dumps into DiscourseDB.
 * 
 * This starter class requires three mandatory parameters: DiscourseName DataSetName piazzaDumpPath
 *
 * The piazzaDumpPath is the path for one piazza dump file (in json format).
 * 
 * 
 * @author Oliver Ferschke
 * @author Haitian Gong
 *
 */
@Configuration
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.piazza.converter"})
public class PiazzaConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName   the name of the dicourse
	 *     DataSetName     the name of the dataset
	 *     piazzaDumpPath  the path of the piazza JSON dump file
	 */
	
	public static void main(String... args) {
		Assert.isTrue(args.length==3, "Usage: PiazzaConverterApplication <DiscourseName> <piazza json dump>");		
		SpringApplication.run(PiazzaConverterApplication.class, args);		
	}

}
