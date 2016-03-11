package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing blog data collected by prosolo
 * from a json dump into DiscourseDB.
 * 
 * This starter class requires four or five parameters: <DiscourseName> <DataSetName> <blogDump> <userMapping> <dumpIsWrappedInJsonArray (optional, default=false)
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.prosolo.blog"})
public class BlogConverterApplication {
	
	/**
	 * @param args <DiscourseName> <DataSetName> <inputFile>
	 */
	public static void main(String[] args) {
        if(args.length<4){
        	throw new IllegalArgumentException("USAGE: BlogConverterApplication <DiscourseName> <DataSetName> <blogDump> <userMapping> <dumpIsWrappedInJsonArray (optional, default=false)>");
        }
		SpringApplication.run(BlogConverterApplication.class, args);       
	}
}
