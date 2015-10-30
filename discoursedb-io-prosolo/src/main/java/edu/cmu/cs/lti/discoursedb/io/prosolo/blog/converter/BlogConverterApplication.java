package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing blog data collected by prosolo<br/>
 * from a json dump into DiscourseDB.<br/>
 * <br/>
 * This starter class requires three mandatory and up to two additional optional parameters: <DiscourseName> <DataSetName> <blogDump> <userMapping (optional)> <dumpIsWrappedInJsonArray (optional, default=false)> <br/>
 * <br/>
 * The user mapping is a csv file that maps a blog author name to an edX user name. It is assumed the file has a header.<br/>
 * If no mapping file is provided, new user entities are created using the author name as a user name.
 * <br/>
 * The dumpIsWrappedInJsonArray parameter identifies whether the whole json dataset is wrapped in a json array (i.e. it begins with "[" and ends with "]") or not.<br/>
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
        if(args.length<3){
        	throw new IllegalArgumentException("USAGE: BlogConverterApplication <DiscourseName> <DataSetName> <blogDump> <userMapping (optional)> <dumpIsWrappedInJsonArray (optional, default=false)>");
        }
		SpringApplication.run(BlogConverterApplication.class, args);       
	}
}
