package edu.cmu.cs.lti.discoursedb.io.habworlds.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


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
		SpringApplication.run(HabworldsConverterApplication.class, args);
	}

}
