package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Status;

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
	

	/**
	 * Maps a Tweet represented as a Twitter4J Status object to DiscourseDB
	 * 
	 * @param discourseName the name of the discourse
	 * @param datasetName the dataset identifier
	 * @param tweet the Tweet to store in DiscourseDB
	 */
	public void mapTweet(String discourseName, String datasetName, Status tweet ) {
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(datasetName, "The dataset name has to be specified and cannot be empty.");
		Assert.notNull(tweet, "The tweet to be mapped to DiscourseDB cannot be null.");
		
		if(dataSourceService.dataSourceExists(String.valueOf(tweet.getId()), TweetSourceMapping.ID_TO_CONTRIBUTION, datasetName)){
			log.trace("Tweet with id "+tweet.getId()+" already exists in database. Skipping");
			return;
		}
		log.trace("Mapping Tweet "+tweet.getId());		
		
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
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
			if(tUser.getDescription()!=null){
				annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getDescription()), "description"));				
			}
			annoService.addAnnotation(user, userInfo);			
		}
		
		Contribution curContrib = contributionService.createTypedContribution(ContributionTypes.TWEET);
		DataSourceInstance contribSource = dataSourceService.createIfNotExists(new DataSourceInstance(String.valueOf(tweet.getId()),TweetSourceMapping.ID_TO_CONTRIBUTION,datasetName));
		curContrib.setStartTime(tweet.getCreatedAt());
		dataSourceService.addSource(curContrib, contribSource);		

		
		AnnotationInstance tweetInfo = annoService.createTypedAnnotation("twitter_tweet_info");
		if(tweet.getSource()!=null){
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

		//TODO this has to be represented as a relation
		if(tweet.getInReplyToStatusId()>0){
			annoService.addFeature(tweetInfo, annoService.createTypedFeature(String.valueOf(tweet.getInReplyToStatusId()), "in_reply_to_status_id"));			
		}		

		//TODO this has to be represented as a relation
		if(tweet.getInReplyToScreenName()!=null){
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
			if(place.getGeometryType()!=null){
				annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getGeometryType()), "geo_type"));				
			}
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getBoundingBoxType()), "bounding_box_type"));
			annoService.addFeature(placeAnno, annoService.createTypedFeature(String.valueOf(place.getFullName()), "place_name"));
			if(place.getStreetAddress()!=null){
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
}
