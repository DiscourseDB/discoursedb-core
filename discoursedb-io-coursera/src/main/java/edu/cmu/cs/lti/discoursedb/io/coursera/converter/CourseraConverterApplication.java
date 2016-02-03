package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.coursera.converter.CourseraConverterApplication;

/**
 * This starter class launches the components necessary for importing coursera forum data
 * from a designated Coursera database into DiscourseDB.
 * 
 * This starter class requires six parameters: DataSetName, DiscourseName, coursera_dbhost, coursera_db, coursera_dbuser, coursera_dbpwd
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.coursera"})
public class CourseraConverterApplication {

	/**
	 * @param args 
	 *         DataSetName, DiscourseName, coursera_dbhost, coursera_db, coursera_dbuser, coursera_dbpwd
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length==6,"Usage: CourseraConverterApplication <DataSetName> <DiscourseName> <coursera_dbhost> <coursera_db> <coursera_dbuser> <coursera_dbpwd>");
		SpringApplication.run(CourseraConverterApplication.class, args);
		
	}

}
