package edu.cmu.cs.lti.discoursedb.io.twitter.api;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

public class TweetParser {

	public static void main(String[] args) throws IOException, TwitterException{
		String json = FileUtils.readFileToString(new File("/home/oliverf/Desktop/tweet.json.mongo"));
		Status s = parseJson(json);
		System.out.println(s.getId());
		System.out.println(s.getText());
	}
	
	public static Status parseJson(String json) throws TwitterException{		
		return TwitterObjectFactory.createStatus(json);
	}
	
}
