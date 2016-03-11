package edu.cmu.cs.lti.discoursedb.io.tags.converter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Haitian Gong
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.tags"})
public class TweetConverterApplication {

	public static void main(String[] args) throws Exception {		
		
		SpringApplication.run(TweetConverterApplication.class, args);

	}

}
