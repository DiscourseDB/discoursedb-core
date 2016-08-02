package edu.cmu.cs.lti.discoursedb.io.pems.converter;

import java.text.ParseException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mongodb.Block;
import com.mongodb.MongoClient;

import lombok.extern.log4j.Log4j;


@Log4j
@Component
public class PemsConverter implements CommandLineRunner {
	
	@Autowired 
	PemsConverterService converterService;
	
	@Override
	public void run(String... args) throws ParseException {		
		Assert.isTrue(args.length == 5, "Usage: PemsConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoDatabaseName> <MongoCollectionName");
		
		String discourseName = args[0];
		String datasetName = args[1];		
		String dbHost = args[2];
		String dbName = args[3];
		String collectionName = args[4];		
		
		this.convert(discourseName, datasetName, dbHost, dbName, collectionName);
	}
	
	/**
	 * Opens a connection to a MongoDB instance, reads all Documents in the provided collection of tweets and passes them on to the Tweet mapper.
	 * 
	 * @param discourseName the name of the DiscourseDB discourse 
	 * @param datasetName the database identifier
	 * @param dbHost the host of the database server (e.g. "localhost") 
	 * @param dbName the name of the database containing the tweet collection
	 * @param collectionName the name of the collection containing the tweet documents
	 */
	private void convert(String discourseName, String datasetName, String dbHost, String dbName, String collectionName) {
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");
		Assert.hasText(dbHost, "The MongoDB database host has to be specified and cannot be empty.");
		Assert.hasText(dbName, "The database name has to be specified and cannot be empty.");
		Assert.hasText(collectionName, "The collection name has to be specified and cannot be empty.");
		
		MongoClient mongoClient = new MongoClient(dbHost); //assuming standard port
		
		log.info("Starting to import traffic data from MongoDB database \""+dbName+"\" collection \""+collectionName+"\"");
		
		mongoClient.getDatabase(dbName).getCollection(collectionName).find().forEach((Block<Document>) d -> {
			converterService.mapDocument(discourseName,datasetName,d);
		});

		log.info("Finished importing pems data.");	
		
		mongoClient.close();		
	}
	

}	
