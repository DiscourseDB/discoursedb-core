package edu.cmu.cs.lti.discoursedb.io.salon.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.salon.converter.SalonConverterApplication;

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.salon"})
public class SalonConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the discourse
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length == 2, "Usage: Usage: SalonConverterApplication <DiscourseName> <SalonID>");
		SpringApplication.run(SalonConverterApplication.class, args);
	}
	
}