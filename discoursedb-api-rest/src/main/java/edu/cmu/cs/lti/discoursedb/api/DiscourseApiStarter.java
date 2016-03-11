package edu.cmu.cs.lti.discoursedb.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.config.EnableEntityLinks;

/**
 * A SpringBootApplication that launches a server that hosts the API.
 *
 */
@SpringBootApplication
@EnableEntityLinks
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class DiscourseApiStarter {

	public static void main(String[] args) {
		SpringApplication.run(DiscourseApiStarter.class, args);
	}

}
