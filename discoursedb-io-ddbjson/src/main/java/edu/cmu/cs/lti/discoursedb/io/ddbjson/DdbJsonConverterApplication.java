package edu.cmu.cs.lti.discoursedb.io.ddbjson;

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
		"edu.cmu.cs.lti.discoursedb.io.ddbjson"})
public class DdbJsonConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     directoryOfCsvFiles     the path of directory with Coursera SQL import files
	 *     datasetname      what to name this dataset
	 */
	
	public static void main(String[] args) {		
		Assert.isTrue(args.length==1,"Usage: DdbJsonConverterApplication <ddbJson file>");
		SpringApplication.run(DdbJsonConverterApplication.class, args);		
	}

}