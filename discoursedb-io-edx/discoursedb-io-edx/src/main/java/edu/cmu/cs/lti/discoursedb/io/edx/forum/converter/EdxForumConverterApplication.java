package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an EdX forum dump into DiscourseDB.
 * Currently, it only launches only EdxForumConverter. 
 * 
 * @author Oliver Ferschke
 */
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class EdxForumConverterApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
