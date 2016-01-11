package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an EdX forum dump into DiscourseDB.
 * In particular, it launches the EdxForumConverter which uses the EdxForumConverterService to map data to DiscourseDB.
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.edx.forum"})
public class EdxForumConverterApplication {
	
	private static final Logger logger = LogManager.getLogger(EdxForumConverterApplication.class);

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql>(optional)
	 */
	public static void main(String[] args) {
		if(args.length<2){
			logger.error("Usage: EdxForumConverterApplication <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql> (optional)");
			return;
		}
        SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
