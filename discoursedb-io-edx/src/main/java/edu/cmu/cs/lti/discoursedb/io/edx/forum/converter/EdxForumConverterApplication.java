package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

/**
 * This starter class launches the components necessary for importing an EdX forum dump into DiscourseDB.
 * In particular, it launches the EdxForumConverter which uses the EdxForumConverterService to map data to DiscourseDB.
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.edx.forum"})
public class EdxForumConverterApplication {

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql>(optional)
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >= 2, "Usage: EdxForumConverterApplication <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql> (optional)");
        SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
