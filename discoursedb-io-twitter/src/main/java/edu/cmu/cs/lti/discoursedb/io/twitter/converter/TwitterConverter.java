package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
		
		if (args.length != 3) {
			logger.error("Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDatabaseName> <MongoCollectionName");
			return;
		}
		
		String discourseName = args[0];
		String datasetName = args[1];		
		String dbName = args[2];
		String collectionName = args[3];		
		
		logger.info("Starting Twitter data conversion");
		this.convert(discourseName, datasetName, dbName, collectionName);
		logger.info("Twitter data conversion completed");
	}
	
	private void convert(String discourseName, String datasetName, String dbName, String collectionName) {
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(dbName);
		MongoCollection<Document> col = db.getCollection(collectionName);
		
		MongoCursor<Document> it = col.find().iterator();
		while(it.hasNext()){
			Document curdoc = it.next();
			try{
				Status curStatus = TwitterObjectFactory.createStatus(curdoc.toJson());
				System.out.println(curStatus.getCreatedAt());
				//TODO process curStatus
			}catch(TwitterException e){
				log.warn("Could not parse tweet",e);
			}
		}
		mongoClient.close();
		
	}

}
