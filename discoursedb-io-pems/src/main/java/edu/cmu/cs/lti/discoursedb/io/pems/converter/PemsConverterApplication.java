package edu.cmu.cs.lti.discoursedb.io.pems.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.twitter"})
public class PemsConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     MongoDbHost		the hostname of the mongo db server
	 *     MongoDatabaseName  the name of the mongo database
	 *     MongoCollectionName  the name of the collection that contains the traffic data
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length==5, "Usage: PemsConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoDatabaseName> <MongoCollectionName");
		SpringApplication.run(PemsConverterApplication.class, args);
	}

}
