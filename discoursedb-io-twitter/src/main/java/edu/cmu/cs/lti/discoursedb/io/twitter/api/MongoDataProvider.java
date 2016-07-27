package edu.cmu.cs.lti.discoursedb.io.twitter.api;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import twitter4j.Status;

public class MongoDataProvider {

	public static void main(String[] args) throws Exception{
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase( "superbowl_sample" );
		MongoCollection<Document> col = db.getCollection("superbowl");
		MongoCursor<Document> it = col.find().iterator();
		while(it.hasNext()){
			Document curdoc = it.next();
			Status curStatus = TweetParser.parseJson(curdoc.toJson());
			//System.out.println(curStatus.getText());
			System.out.println(curStatus.getCreatedAt());
		}
		mongoClient.close();
	}

}
