package edu.cmu.cs.lti.discoursedb.core.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class DiscourseDBCoreAPI {
    public static void main(String[] args) {
        SpringApplication.run(DiscourseDBCoreAPI.class, args);
    }

}
