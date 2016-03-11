package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

/**
 * This starter class launches the components necessary for importing an Wikipedia Talk page data
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.wikipedia.talk"})
public class WikipediaTalkPageConverterApplication {
	
	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DiscourseName> <DataSetName> <tileListFile> <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length==8,"Usage: WikipediaTalkPageConverterApplication <DiscourseName> <DataSetName> <tileListFile> <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>");
        SpringApplication.run(WikipediaTalkPageConverterApplication.class, args);       
	}
}
