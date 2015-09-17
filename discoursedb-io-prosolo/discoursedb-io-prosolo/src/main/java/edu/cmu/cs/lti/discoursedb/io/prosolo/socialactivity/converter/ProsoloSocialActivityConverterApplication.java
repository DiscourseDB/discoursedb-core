package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import javax.transaction.Transactional;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing prosolo social activities
 * from a csv dump into DiscourseDB.
 * 
 * This starter class requires one parameter: a pointer to the csv dump of the social activity table.
 * 
 * @author Oliver Ferschke
 */
@Transactional
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity"})
public class ProsoloSocialActivityConverterApplication {
	
	/**
	 * @param args {"social_activity.csv"}
	 */
	public static void main(String[] args) {
        if(args.length!=6){
        	throw new IllegalArgumentException("USAGE: ProsoloSocialActivityConverterApplication <DiscourseName> <DiscourseDescriptor> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
        }
		SpringApplication.run(ProsoloSocialActivityConverterApplication.class, args);       
	}
}
