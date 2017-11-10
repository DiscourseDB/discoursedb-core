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

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import edu.cmu.cs.lti.discoursedb.io.twitter.model.PemsStationMetaData;
import lombok.extern.log4j.Log4j;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Log4j
@Component
public class TwitterConverter implements CommandLineRunner {

	@Autowired
	TwitterConverterService converterService;

	private final static int MAX_DIST = 5000;

	MongoClient mongoClient;

	@Override
	public void run(String... args) throws ParseException {
		Assert.isTrue(args.length == 5 || args.length == 7,
				"Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoTwitterDatabaseName> <MongoTwitterCollectionName> [ <PemsMetaMongoDataDatabaseName> <PemsMetaDataMongoCollectionName> optional]");

		String discourseName = args[0];
		String datasetName = args[1];
		String dbHost = args[2];
		String twitterDbName = args[3];
		String twitterCollectionName = args[4];
		String pemsMetaDbName = null;
		String pemsMetaCollectionName = null;
		if (args.length == 7) {
			pemsMetaDbName = args[5];
			pemsMetaCollectionName = args[6];
		}

		// start data import
		this.convert(discourseName, datasetName, dbHost, twitterDbName, twitterCollectionName, pemsMetaDbName,
				pemsMetaCollectionName);

		// retrieve timelines for all imported users
		this.retrieveTimelines(discourseName, datasetName, dbHost, twitterDbName, twitterCollectionName);

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
			String twitterCollectionName, String pemsDB, String pemsCollection) {
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");
		Assert.hasText(dbHost, "The MongoDB database host has to be specified and cannot be empty.");
		Assert.hasText(twitterDbName, "The database name has to be specified and cannot be empty.");
		Assert.hasText(twitterCollectionName, "The collection name has to be specified and cannot be empty.");

		mongoClient = new MongoClient(dbHost); // assuming standard port

		log.info("Starting to import tweets from MongoDB database \"" + twitterDbName + "\" collection \""
				+ twitterCollectionName + "\"");

		mongoClient.getDatabase(twitterDbName).getCollection(twitterCollectionName).find()
				.forEach((Block<Document>) d -> {
					if (pemsDB != null && pemsCollection != null) {
						// add PEMS station meta data
						converterService.mapTweet(discourseName, datasetName, document2Tweet(d),
								document2Pems(d, pemsDB, pemsCollection));
					} else {
						// do not add PEMS station meta data
						converterService.mapTweet(discourseName, datasetName, document2Tweet(d), null);
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

		Document location = (Document) tweetDocument.get("location");
		Document place = (Document) tweetDocument.get("place");
		if (location != null && !location.isEmpty()) {
			// if available, map gps tag to station
			log.trace("Mapping gps location to PEMS station data.");

			BasicDBObject query = (BasicDBObject) JSON.parse("{location:{$near:{$geometry:" + location.toJson()
					+ ",$minDistance:0,$maxDistance: " + MAX_DIST + "}}}");
			Document metaData = (Document) mongoClient.getDatabase(pemsMetaDb).getCollection(pemsMetaCollection)
					.find(query).first();
			pems = new PemsStationMetaData(metaData);
		} else if (place != null && !place.isEmpty()) {
			// if no gps tag is available, map place to station if available
			log.trace("Mapping place tag to PEMS station data.");
			log.warn("Not yet implemented yet."); // TODO implement place
													// mapping

		} else {
			// no gps tag or place available - do nothing for now
			log.trace("No location or place available for PEMS station data mapping.");
		}

		return pems;
	}

	private void retrieveTimelines(String discourseName, String datasetName, String dbHost, String twitterDbName,
			String twitterCollectionName) {

		List<String> usernames = new ArrayList<>();
		mongoClient = new MongoClient(dbHost); // assuming standard port

		log.debug("Import usernames from MongoDB database \"" + twitterDbName + "\" collection \"" + twitterCollectionName + "\"");		
		mongoClient.getDatabase(twitterDbName).getCollection(twitterCollectionName).distinct("user.screen_name", String.class).into(usernames);
		
		converterService.importUserTimelines(usernames, discourseName, datasetName);
	}

}
