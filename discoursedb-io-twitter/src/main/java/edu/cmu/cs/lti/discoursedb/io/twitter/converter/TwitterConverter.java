package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import java.text.ParseException;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mongodb.Block;
import com.mongodb.MongoClient;

import lombok.extern.log4j.Log4j;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;


@Log4j
@Component
public class TwitterConverter implements CommandLineRunner {
	
	@Autowired 
	TwitterConverterService converterService;
	
	@Override
	public void run(String... args) throws ParseException {		
		Assert.isTrue(args.length == 5, "Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoDatabaseName> <MongoCollectionName");
		
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
		
		log.info("Starting to import tweets from MongoDB database \""+dbName+"\" collection \""+collectionName+"\"");
		
		mongoClient.getDatabase(dbName).getCollection(collectionName).find().forEach((Block<Document>) d -> {
			mapTweet(discourseName, datasetName, document2Tweet(d));
		});

		log.info("Finished importing tweets.");
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
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");

		//skip tweets that are null. Warnings are already emitted in parseDocument(). 
		if(tweet!=null){
			converterService.mapTweet(discourseName,datasetName,tweet);			
		}
	}
	
	/**
	 * Parses a MongoDB Document that represents a tweet into a Twitter4J Status object
	 * 
	 * @param tweetDocument a MongoDB document representing a tweet
	 * @return a Twitter4J Status object representing the tweet
	 */
	private Status document2Tweet(Document tweetDocument){
		Assert.notNull(tweetDocument, "The mongodb document representing the tweet to be parsed cannot be null.");

		Status stat = null; //if parsing fails, null will be returns and mapper will skip the tweet 
		try{
			stat = TwitterObjectFactory.createStatus(tweetDocument.toJson());			
		}catch(TwitterException e){
			log.warn("Could not parse tweet from document", e);
		}		
		return stat;
	}
}	
