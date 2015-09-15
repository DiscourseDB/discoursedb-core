package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import javax.transaction.Transactional;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an EdX forum dump into DiscourseDB.
 * Currently, it launches the two phases of the EdxForumConverter.
 * 
 * @author Oliver Ferschke
 */
@Transactional
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class EdxForumConverterApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
