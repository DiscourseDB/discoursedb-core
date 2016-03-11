package edu.cmu.cs.lti.discoursedb.annotation.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class AnnotationUIStarter {
	public static void main(String[] args) {
		SpringApplication.run(AnnotationUIStarter.class, args);
	}
}
