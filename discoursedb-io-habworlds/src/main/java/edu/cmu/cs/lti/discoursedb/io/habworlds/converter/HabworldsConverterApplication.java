package edu.cmu.cs.lti.discoursedb.io.habworlds.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.habworlds"})
public class HabworldsConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     HabworldsFilePath  the path of open-ended discussions csv file 
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length==3, "Usage: HabworldsConverterApplication <DiscourseName> <DataSetName> <HabworldsFilePath>");
		SpringApplication.run(HabworldsConverterApplication.class, args);
	}

}
