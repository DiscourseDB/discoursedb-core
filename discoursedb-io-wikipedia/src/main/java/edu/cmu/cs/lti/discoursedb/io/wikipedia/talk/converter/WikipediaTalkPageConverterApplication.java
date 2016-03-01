package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.log4j.Log4j;

/**
 * This starter class launches the components necessary for importing an Wikipedia Talk page data
 * 
 * @author Oliver Ferschke
 */
@Log4j
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.wikipedia.talk"})
public class WikipediaTalkPageConverterApplication {
	
	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		if(args.length!=8){
			log.error("Usage: WikipediaTalkPageConverterApplication <DiscourseName> <DataSetName> <tileListFile> <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>");
			return;
		}
        SpringApplication.run(WikipediaTalkPageConverterApplication.class, args);       
	}
}
