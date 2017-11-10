package edu.cmu.cs.lti.discoursedb.io.courseraUPenn.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

/**
 * 
 * This starter class launches the components necessary for importing Coursera data into DiscourseDB.
 * 
 * The expected input files are a directory full of coursera CSV import files storing course and 
 * discussion information.  
 *  
 * @author Chris Bogart
 *
 */

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration",
		"edu.cmu.cs.lti.discoursedb.io.courseraUPenn"})
public class CourseraConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     directoryOfCsvFiles     the path of directory with Coursera SQL import files
	 *     datasetname      what to name this dataset
	 */
	
	public static void main(String[] args) {		
		Assert.isTrue(args.length==2,"Usage: CourseraConverterApplication <directoryOfCsvFiles> <datasetname>");
		SpringApplication.run(CourseraConverterApplication.class, args);		
	}

}