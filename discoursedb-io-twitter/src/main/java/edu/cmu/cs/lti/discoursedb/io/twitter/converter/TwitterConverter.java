/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import edu.cmu.cs.lti.discoursedb.io.twitter.model.PemsStationMetaData;
import lombok.extern.log4j.Log4j;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.auth.AccessToken;

@Log4j
@Component
public class TwitterConverter implements CommandLineRunner {

	@Autowired
	TwitterConverterService converterService;
	@Autowired Environment environment;
	
	private final static int MAX_DIST = 5000;

	MongoClient mongoClient;

	@Override
	public void run(String... args) throws ParseException {
		Assert.isTrue(args.length == 5 || (args.length >= 6 && args.length <=8),
				"Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoTwitterDatabaseName> <MongoTwitterCollectionName> [ <PemsMetaMongoDataDatabaseName> <PemsMetaDataMongoCollectionName> optional]");

		String discourseName = args[0];
		String datasetName = args[1];
		String dbHost = args[2];
		String twitterDbName = args[3];
		String twitterCollectionName = args[4];
		String pemsMetaDbName = null;
		String pemsMetaCollectionName = null;
		String stationsLocationFilePath = null;
		if(args.length >= 6)
			stationsLocationFilePath = args[5];
		if (args.length == 8) {
			pemsMetaDbName = args[6];
			pemsMetaCollectionName = args[7];
		}
		
		// start data import
		this.convert(discourseName, datasetName, dbHost, twitterDbName, twitterCollectionName, stationsLocationFilePath,
				pemsMetaDbName,pemsMetaCollectionName);

		// retrieve timelines for all imported users
		this.retrieveTimelines(discourseName, datasetName, dbHost, twitterDbName, twitterCollectionName, pemsMetaDbName,
				pemsMetaCollectionName);

	}

	/**
	 * Opens a connection to a MongoDB instance, reads all Documents in the
	 * provided collection of tweets and passes them on to the Tweet mapper.
	 * 
	 * @param discourseName
	 *            the name of the DiscourseDB discourse
	 * @param datasetName
	 *            the database identifier
	 * @param dbHost
	 *            the host of the database server (e.g. "localhost")
	 * @param dbName
	 *            the name of the database containing the tweet collection
	 * @param collectionName
	 *            the name of the collection containing the tweet documents
	 */
	private void convert(String discourseName, String datasetName, String dbHost, String twitterDbName,
			String twitterCollectionName, String stationLocations, String pemsDB, String pemsCollection) {
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");
		Assert.hasText(dbHost, "The MongoDB database host has to be specified and cannot be empty.");
		Assert.hasText(twitterDbName, "The database name has to be specified and cannot be empty.");
		Assert.hasText(twitterCollectionName, "The collection name has to be specified and cannot be empty.");

		mongoClient = new MongoClient(dbHost); // assuming standard port

		log.info("Starting to import tweets from MongoDB database \"" + twitterDbName + "\" collection \""
				+ twitterCollectionName + "\"");

		mongoClient.getDatabase(twitterDbName).getCollection(twitterCollectionName).find().noCursorTimeout(true)
				.forEach((Block<Document>) d -> {
					if(stationLocations != null)
					{
						if (pemsDB != null && pemsCollection != null) {
							// add PEMS station meta data
							converterService.mapTweet(discourseName, datasetName, document2Tweet(d),
									stationLocations, document2Pems(d, pemsDB, pemsCollection));
						}
						else
						{
							// do not add PEMS station meta data
							converterService.mapTweet(discourseName, datasetName, document2Tweet(d), stationLocations, null);
						} 
					}else {
						// do not add station locations and PEMS station meta data
						converterService.mapTweet(discourseName, datasetName, document2Tweet(d),null, null);
					}
				});

		log.info("Finished importing tweets.");

		// TODO we need a second pass to create relations between tweets.
		// right now, they are stored as annotations (ids).
		// we might be able to collect information about the relations in the
		// first pass that we can use to
		// create relations without doing a full iteration over the whole mongo
		// database again

		mongoClient.close();
	}

	/**
	 * Parses a MongoDB Document that represents a tweet into a Twitter4J Status
	 * object
	 * 
	 * @param tweetDocument
	 *            a MongoDB document representing a tweet
	 * @return a Twitter4J Status object representing the tweet
	 */
	private Status document2Tweet(Document tweetDocument) {
		Assert.notNull(tweetDocument, "The mongodb document representing the tweet to be parsed cannot be null.");

		Status stat = null; // if parsing fails, null will be returns and mapper
							// will skip the tweet
		try {
			stat = TwitterObjectFactory.createStatus(tweetDocument.toJson());
		} catch (TwitterException e) {
			log.warn("Could not parse tweet from document", e);
		}
		return stat;
	}

	/**
	 * 
	 * @param tweetDocument
	 *            a MongoDB document representing a tweet
	 * @param pemsMetaDb
	 *            database with the pems data
	 * @param pemsMetaCollection
	 *            collection with the pems meta data
	 * @param pemsdatacollection
	 *            collection with the pems station data
	 * @return an object containing the pems station data to be mapped to the
	 *         tweet
	 */
	private PemsStationMetaData document2Pems(Document tweetDocument, String pemsMetaDb, String pemsMetaCollection) {

		Assert.notNull(tweetDocument, "The mongodb document representing the tweet to be parsed cannot be null.");
		PemsStationMetaData pems = null;

		Document coordinates = (Document) tweetDocument.get("coordinates");
		Document place = (Document) tweetDocument.get("place");
		if (coordinates != null && !coordinates.isEmpty()) {
			// if available, map gps tag to station
			log.trace("Mapping gps coordinates to PEMS station data.");

			BasicDBObject query = (BasicDBObject) JSON.parse("{location:{$near:{$geometry:" + coordinates.toJson()
					+ ",$minDistance:0,$maxDistance: " + MAX_DIST + "}}}");
			Document metaData = (Document) mongoClient.getDatabase(pemsMetaDb).getCollection(pemsMetaCollection)
					.find(query).first();
                        if (metaData != null) {
                            log.trace("Assigning PEMS data to a tweet");
			    pems = new PemsStationMetaData(metaData);
                        } else {
                            log.trace("NOT assigning PEMS data to a tweet");
                        }
		} else if (place != null && !place.isEmpty()) {
			// if no gps tag is available, map place to station if available
			log.trace("Mapping place tag to PEMS station data.");
			log.warn("Mapping 'place' tweet tag to PEMS location: Not yet implemented"); // TODO implement place
													// mapping

		} else {
			// no gps tag or place available - do nothing for now
			log.trace("No location or place available for PEMS station data mapping.");
		}

		return pems;
	}
	
	/**
	 * 
	 * @param tweetDocument
	 *            a MongoDB document representing a tweet
	 * @param pemsMetaDb
	 *            database with the pems data
	 * @param pemsMetaCollection
	 *            collection with the pems meta data
	 * @param pemsdatacollection
	 *            collection with the pems station data
	 * @return an object containing the pems station data to be mapped to the
	 *         tweet
	 */
	private PemsStationMetaData tweet2Pems(Status tweet, String pemsMetaDb, String pemsMetaCollection) {

		Assert.notNull(tweet, "The mongodb document representing the tweet to be parsed cannot be null.");
		PemsStationMetaData pems = null;

		GeoLocation geo = tweet.getGeoLocation();
		Place place = tweet.getPlace();
		if (geo != null) {
			// if available, map gps tag to station
			log.trace("Mapping gps coordinates to PEMS station data.");

			String queryString ="{location:{$near: { $geometry: { type: \"Point\",  coordinates: [" 
					+ String.valueOf(geo.getLongitude()) + "," 
					+ String.valueOf(geo.getLatitude()) + "] } "
					+ ",$minDistance:0,$maxDistance: " + MAX_DIST + "}}}";
			BasicDBObject query = (BasicDBObject) JSON.parse(queryString);
			log.trace(queryString);
			Document metaData = (Document) mongoClient.getDatabase(pemsMetaDb).getCollection(pemsMetaCollection)
					.find(query).first();
                        if (metaData != null) {
                            log.info("Assigning PEMS data to a tweet");
			    pems = new PemsStationMetaData(metaData);
                        } else {
                            log.info("NOT assigning PEMS data to a tweet");
                        }
		} else if (place != null) {
			// if no gps tag is available, map place to station if available
			log.trace("Mapping place tag to PEMS station data.");
			log.warn("Parsing 'place' in tweet: Not yet implemented"); // TODO implement place
													// mapping

		} else {
			// no gps tag or place available - do nothing for now
			log.info("No geolocation or place available for PEMS station data mapping.");
		}

		return pems;
	}

	/**
	 * For each user in the mongodb dataset, import the whole timeline of that user (API limit: latest 3,200 tweets)
	 * 
	 * @param users
	 * @param discourseName
	 * @param datasetName
	 */
	public void importUserTimelines(List<String> users, String discourseName, String datasetName, 
			String pemsDB, String pemsCollection){
		Twitter twitter = TwitterFactory.getSingleton();
            	if (!twitter.getAuthorization().isEnabled()) {
                	twitter.setOAuthConsumer(environment.getRequiredProperty("oauth.consumerKey"),
                        	environment.getRequiredProperty("oauth.consumerSecret"));
                	twitter.setOAuthAccessToken(new AccessToken(environment.getRequiredProperty("oauth.accessToken"),
                        	environment.getRequiredProperty("oauth.accessTokenSecret")));
            	}
		
		log.info("Importing timelines for "+users.size()+" users into DiscourseDB");
		
		for(String screenname:users){
		    log.info("Retrieving timeline for user "+screenname);
			List<Status> tweets = new ArrayList<>();
			
	    	//There's an API limit of 3,200 tweets you can get from a timeline and 200 per request (page). 
		    //This makes 16 requests with 200 tweets per page (pages 1 to 17)
			//This also works if the users has less than 3,200 tweets
		    for(int i=1;i<17;i++){  
			    try{
			    	tweets.addAll(twitter.getUserTimeline(screenname, new Paging (i, 200)));			    	
			    }catch(TwitterException e){
			    	log.error("Error retrieving timeline for user "+screenname,e);
			    }
		    }

		    log.info("Retrieved timeline ("+tweets.size()+" Tweets) for user "+screenname);
		    log.info("Mapping tweets for user "+screenname);
		    for(Status tweet:tweets){
			    log.info("Mapping tweet "+tweet.getId());
		    	converterService.mapTweet(discourseName, datasetName, tweet, null, tweet2Pems(tweet, pemsDB, pemsCollection));
		    }
		}
	}
	
	
	private void retrieveTimelines(String discourseName, String datasetName, String dbHost, String twitterDbName,
			String twitterCollectionName, String pemsDB, String pemsCollection) {

		List<String> usernames = new ArrayList<>();
		mongoClient = new MongoClient(dbHost); // assuming standard port

		log.debug("Import usernames from MongoDB database \"" + twitterDbName + "\" collection \"" + twitterCollectionName + "\"");		
		mongoClient.getDatabase(twitterDbName).getCollection(twitterCollectionName).distinct("user.screen_name", String.class).into(usernames);
		
		importUserTimelines(usernames, discourseName, datasetName,  pemsDB,  pemsCollection);
	}

}
