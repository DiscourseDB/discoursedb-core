package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.extern.log4j.Log4j;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;


@Log4j
@Component
public class TwitterConverter implements CommandLineRunner {
	
	private static final Logger logger = LogManager.getLogger(TwitterConverter.class);
	
	@Autowired 
	TwitterConverterService converterService;
	
	@Override
	public void run(String... args) throws ParseException {
		
		if (args.length != 5) {
			logger.error("Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoDatabaseName> <MongoCollectionName");
			return;
		}
		
		String discourseName = args[0];
		String datasetName = args[1];		
		String dbHost = args[2];
		String dbName = args[3];
		String collectionName = args[4];		
		
		logger.info("Starting Twitter data conversion");
		this.convert(discourseName, datasetName, dbHost, dbName, collectionName);
		logger.info("Twitter data conversion completed");
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
		
		MongoClient mongoClient = new MongoClient(dbHost); //assuming standard port
		MongoDatabase db = mongoClient.getDatabase(dbName);
		MongoCollection<Document> col = db.getCollection(collectionName);
		
		col.find().forEach((Block<Document>) d -> {
			mapTweet(discourseName, datasetName, parseDocument(d));
		});
		
		mongoClient.close();		
	}
	
	/**
	 * Maps a tweet represented as a Twitter4J Status object to DiscourseDB
	 * 
	 * @param discourseName the name of the discourse
	 * @param datasetName the dataset identifier
	 * @param tweet the Tweet to store in DiscourseDB
	 */
	private void mapTweet(String discourseName, String datasetName, Status tweet ) {
		//TODO process tweet and store in DiscourseDB
	}
	
	/**
	 * Parses a MongoDB Document that represents a tweet into a Twitter4J Status object
	 * 
	 * @param tweetDocument a MongoDB document representing a tweet
	 * @return a Twitter4J Status object representing the tweet
	 */
	private Status parseDocument(Document tweetDocument){
		Status stat = null;
		try{
			stat = TwitterObjectFactory.createStatus(tweetDocument.toJson());			
		}catch(TwitterException e){
			log.warn("Could not parse tweet from document", e);
		}		
		return stat;
	}
}	
