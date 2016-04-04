package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

/**
 * 
 * This starter class launches the components necessary for importing an Bazaar chatroom data into DiscourseDB.
 * In particular, it launches the BazaarConverter which uses the BazaarConverterService to map data to DiscourseDB.
 * 
 * The expected input files are two csv spreadsheets storing chat information and room information.
 * Field delimiters should be commas and the encoding should be UTF-8.
 * 
 *  
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.bazaar"})
public class BazaarConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     ChatFilePath     the path of csv file containing chat information
	 *     RoomFilePath     the path of csv file containing room information
	 */
	
	public static void main(String[] args) {		
		Assert.isTrue(args.length==4,"Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log>");
		SpringApplication.run(BazaarConverterApplication.class, args);		
	}

}
