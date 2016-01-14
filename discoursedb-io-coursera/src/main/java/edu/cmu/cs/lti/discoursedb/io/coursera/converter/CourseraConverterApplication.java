package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.io.coursera.converter.CourseraConverterApplication;

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.coursera"})
public class CourseraConverterApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(CourseraConverterApplication.class, args);
		
	}

}
