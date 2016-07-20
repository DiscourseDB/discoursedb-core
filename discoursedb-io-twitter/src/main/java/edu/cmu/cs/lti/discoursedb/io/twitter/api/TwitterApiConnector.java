package edu.cmu.cs.lti.discoursedb.io.twitter.api;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

@Log4j
public class TwitterApiConnector {

	public static void main(String[] args) throws Exception{
		TwitterApiConnector api = new TwitterApiConnector();
		List<Status> tweets = api.getTimeline(16067430L);
		log.info(tweets.size());

	}

	public List<Status> getTimeline(long userid) throws TwitterException{
	    Twitter twitter = TwitterFactory.getSingleton();
	    

	    List<Status> statuses = new ArrayList<>();
	    for(int i=1;i<17;i++){
		    statuses.addAll(twitter.getUserTimeline(userid, new Paging (i, 200)));
	    }

	    log.debug("Showing timeline ("+statuses.size()+" Tweets) for user "+statuses.get(0).getUser().getName());
	    for(Status status:statuses){
	        log.trace(status.getText());
	    }

	    return statuses;
	}
	
}
