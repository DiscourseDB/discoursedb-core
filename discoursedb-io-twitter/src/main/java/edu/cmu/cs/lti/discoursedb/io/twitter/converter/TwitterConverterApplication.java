package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.twitter"})
public class TwitterConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     TwitterDumpPath  the path to the Twitter dump (JSON file(s))
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length==3, "Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <TwitterDumpPath>");
		SpringApplication.run(TwitterConverterApplication.class, args);
	}

}
