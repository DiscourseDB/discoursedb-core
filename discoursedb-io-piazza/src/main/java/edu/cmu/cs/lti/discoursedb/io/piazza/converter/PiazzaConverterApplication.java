package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


/**
 * @author Oliver Ferschke
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.piazza.converter"})
public class PiazzaConverterApplication {

	public static void main(String... args) {
		Assert.isTrue(args.length==2, "Usage: PiazzaConverterApplication <DiscourseName> <piazza json dump>");		
		SpringApplication.run(PiazzaConverterApplication.class, args);		
	}

}
