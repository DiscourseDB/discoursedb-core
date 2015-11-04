package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an Wikipedia Talk page data
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.wikipedia.talk"})
public class WikipediaTalkPageConverterApplication {
	
	private static final Logger logger = LogManager.getLogger(WikipediaTalkPageConverterApplication.class);

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		if(args.length!=8){
			logger.error("Usage: WikipediaTalkPageConverterApplication <DiscourseName> <DataSetName> <tileListFile> <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>");
			return;
		}
        SpringApplication.run(WikipediaTalkPageConverterApplication.class, args);       
	}
}
