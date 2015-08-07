package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class EdxForumConverterApplication {
	
	public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(EdxForumConverterApplication.class, args);       
	}
}
