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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.twitter.model.PemsStationMetaData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Service for mapping data retrieved from the Twitter4j API to DiscourseDB
 * 
 * @author Oliver Ferschke
 *
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
        @Autowired private org.springframework.core.env.Environment environment;

	

	/**
	 * Maps a Tweet represented as a Twitter4J Status object to DiscourseDB
	 * 
	 * @param discourseName the name of the discourse
	 * @param datasetName the dataset identifier
	 * @param tweet the Tweet to store in DiscourseDB
	 */
	public void mapTweet(String discourseName, String datasetName, Status tweet, PemsStationMetaData pemsMetaData ) {
		if(tweet==null){return;}

		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");

		if(dataSourceService.dataSourceExists(String.valueOf(tweet.getId()), TweetSourceMapping.ID_TO_CONTRIBUTION, datasetName)){
			log.trace("Tweet with id "+tweet.getId()+" already exists in database. Skipping");
			return;
		}
		log.trace("Mapping Tweet "+tweet.getId());		
		
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		DiscoursePart discoursePart = discoursepartService.createOrGetTypedDiscoursePart(discourse,
                          "All Tweets", DiscoursePartTypes.TWEETS);
		twitter4j.User tUser = tweet.getUser();
		User user = null;
		if(!userService.findUserByDiscourseAndUsername(discourse,tUser.getScreenName()).isPresent()){
			user = userService.createOrGetUser(discourse, tUser.getScreenName());
			user.setRealname(tUser.getName());
			user.setEmail(tUser.getEmail());
			user.setLocation(tUser.getLocation());
			user.setLanguage(tUser.getLang());
			user.setStartTime(tweet.getUser().getCreatedAt());
			
			AnnotationInstance userInfo = annoService.createTypedAnnotation("twitter_user_info");
			annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getFavouritesCount()), "favorites_count"));
			annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getFollowersCount()), "followers_count"));
			annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getFriendsCount()), "friends_count"));
			annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getStatusesCount()), "statuses_count"));
			annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getListedCount()), "listed_count"));
			if(tUser.getDescription()!=null && tUser.getDescription().length() > 0){
				annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getDescription()), "description"));				
			}
			annoService.addAnnotation(user, userInfo);			
		} else {
			user = userService.createOrGetUser(discourse, tUser.getScreenName());
                }
		
		Contribution curContrib = contributionService.createTypedContribution(ContributionTypes.TWEET);
		discoursepartService.addContributionToDiscoursePart(curContrib, discoursePart);
		DataSourceInstance contribSource = dataSourceService.createIfNotExists(new DataSourceInstance(String.valueOf(tweet.getId()),TweetSourceMapping.ID_TO_CONTRIBUTION,datasetName));
		curContrib.setStartTime(tweet.getCreatedAt());
		dataSourceService.addSource(curContrib, contribSource);		

		
		AnnotationInstance tweetInfo = annoService.createTypedAnnotation("twitter_tweet_info");
		if(tweet.getSource()!=null && tweet.getSource().length() > 0){
			annoService.addFeature(tweetInfo, annoService.createTypedFeature(tweet.getSource(), "tweet_source"));			
		}
		
		annoService.addFeature(tweetInfo, annoService.createTypedFeature(String.valueOf(tweet.getFavoriteCount()), "favorites_count"));			
		
		if(tweet.getHashtagEntities()!=null){
			for(HashtagEntity hashtag:tweet.getHashtagEntities()){
				annoService.addFeature(tweetInfo, annoService.createTypedFeature(hashtag.getText(), "hashtag"));				
			}
		}

		if(tweet.getMediaEntities()!=null){
			for(MediaEntity media:tweet.getMediaEntities()){
				//NOTE: additional info is available for MediaEntities
				annoService.addFeature(tweetInfo, annoService.createTypedFeature(media.getMediaURL(), "media_url"));				
			}
		}

		//TODO this should be represented as a relation if the related tweet is part of the dataset
		if(tweet.getInReplyToStatusId()>0){
			annoService.addFeature(tweetInfo, annoService.createTypedFeature(String.valueOf(tweet.getInReplyToStatusId()), "in_reply_to_status_id"));			
		}		

		//TODO this should be represented as a relation if the related tweet is part of the dataset
		if(tweet.getInReplyToScreenName()!=null && tweet.getInReplyToScreenName().length() > 0){
			annoService.addFeature(tweetInfo, annoService.createTypedFeature(tweet.getInReplyToScreenName(), "in_reply_to_screen_name"));			
		}		
		annoService.addAnnotation(curContrib, tweetInfo);			

		
		
		GeoLocation geo = tweet.getGeoLocation();
		if(geo!=null){
			AnnotationInstance coord = annoService.createTypedAnnotation("twitter_tweet_geo_location");
			annoService.addFeature(coord, annoService.createTypedFeature(String.valueOf(geo.getLongitude()), "long"));
			annoService.addFeature(coord, annoService.createTypedFeature(String.valueOf(geo.getLatitude()), "lat"));
			annoService.addAnnotation(curContrib, coord);
		}
		
		Place place = tweet.getPlace();
		if(place!=null){
			AnnotationInstance placeAnno = annoService.createTypedAnnotation("twitter_tweet_place");			
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getPlaceType()), "place_type"));
			if(place.getGeometryType()!=null && place.getGeometryType().length() > 0){
				annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getGeometryType()), "geo_type"));				
			}
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getBoundingBoxType()), "bounding_box_type"));
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getFullName()), "place_name"));
			if(place.getStreetAddress()!=null && place.getStreetAddress().length() > 0){
				annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getStreetAddress()), "street_address"));				
			}
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getCountry()), "country"));
			if(place.getBoundingBoxCoordinates()!=null){
				annoService.addFeature(placeAnno, annoService.createTypedFeature(convertGeoLocationArray(place.getBoundingBoxCoordinates()), "bounding_box_lat_lon_array"));							
			}
			if(place.getGeometryCoordinates()!=null){
				annoService.addFeature(placeAnno, annoService.createTypedFeature(convertGeoLocationArray(place.getGeometryCoordinates()), "geometry_lat_lon_array"));							
			}
			annoService.addAnnotation(curContrib, placeAnno);
		}

		Content curContent = contentService.createContent();
		curContent.setText(tweet.getText());
		curContent.setAuthor(user);
		curContent.setStartTime(tweet.getCreatedAt());
		curContrib.setCurrentRevision(curContent);
		curContrib.setFirstRevision(curContent);		

		DataSourceInstance contentSource = dataSourceService.createIfNotExists(new DataSourceInstance(String.valueOf(tweet.getId()),TweetSourceMapping.ID_TO_CONTENT,datasetName));
		dataSourceService.addSource(curContent, contentSource);
					
		if(pemsMetaData!=null){
			AnnotationInstance placeAnno = annoService.createTypedAnnotation("twitter_nearest_pems");			
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(pemsMetaData.getStationId()), "pems_station_id"));
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(pemsMetaData.getLongitude()), "pems_longitude"));
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(pemsMetaData.getLatitdue()), "pems_latitude"));
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(pemsMetaData.getStationName()), "pems_station_name"));
			annoService.addAnnotation(curContrib, placeAnno);
		}
	}
	
	/**
	 * Converts a 2D-array of GeoLocation objects to a list-style String representation of latitude-longitude pairs. 
	 * 
	 * @param location a 2d-array of geolocations representing a point or polygon type bounding box
	 * @return a String representation of the 2D GeoLocation array
	 */
	private String convertGeoLocationArray(GeoLocation[][] location){
		StringBuilder str = new StringBuilder();
		for(int row = 0;row<location.length;row++){
			for(int col = 0;col<location[row].length;col++){
				if(str.length()>0){
					str.append(",");
				}
				str.append("[").append(location[row][col].getLatitude()).append(",").append(location[row][col].getLongitude()).append("]");
			}			
		}
		return str.toString();
	}
	
	/**
	 * For each user in the mongodb dataset, import the whole timeline of that user (API limit: latest 3,200 tweets)
	 * 
	 * @param users
	 * @param discourseName
	 * @param datasetName
	 */
	public void importUserTimelines(List<String> users, String discourseName, String datasetName){
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
		    	mapTweet(discourseName, datasetName, tweet, null);
		    }
		}
	}
}
