package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an EdX forum dump into DiscourseDB.
 * Currently, it launches the two phases of the EdxForumConverter.
 * 
 * @author Oliver Ferschke
 */
@Transactional
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.edx.forum"})
public class EdxForumConverterApplication {
	
	private static final Logger logger = LogManager.getLogger(EdxForumConverterApplication.class);

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DataSetType> <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql>
	 */
	public static void main(String[] args) {
		if(args.length!=3){
			logger.error("Usage: EdxForumConverterApplication <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql>");
			System.exit(1);
		}
        SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
