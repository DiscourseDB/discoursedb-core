package edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterApplication;

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.salontranscripts"})
public class SalonTrConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the discourse
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length == 3, "Usage: Usage: SalonConverterApplication <DiscourseName> <TranscriptDirectory> <Year class took place>");
		SpringApplication.run(SalonTrConverterApplication.class, args);
	}
	
}