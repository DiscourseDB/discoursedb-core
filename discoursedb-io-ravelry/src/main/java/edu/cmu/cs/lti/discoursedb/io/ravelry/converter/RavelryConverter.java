package edu.cmu.cs.lti.discoursedb.io.ravelry.converter;

import java.text.ParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mongodb.MongoClient;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class RavelryConverter implements CommandLineRunner {

	@Autowired RavelryConverterService converterService;
	
	MongoClient mongoClient;
	public String discourseName;
	public String mongoHost;
	public String mongoDbName;
	public String dataSetName;  // Equals mongoDbName
	public String group;
	
	@Override
	public void run(String... args) throws ParseException {
		Assert.isTrue(args.length == 4,
				"Usage: RavelryConverterApplication <DiscourseName> <MongoIP(:port)> <MongoDatabaseName(=DataSet name)> <RavelryGroup(permalink)>");

		discourseName = args[0];
		mongoHost = args[1];
		mongoDbName = args[2];
		dataSetName = mongoDbName;
		group = args[3];		
		// start data import
		
		this.convert_group();

	}
	
	private void convert_group() {
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");
		Assert.hasText(mongoHost, "The MongoDB database host has to be specified and cannot be empty.");
		Assert.hasText(mongoDbName, "The database name has to be specified and cannot be empty.");
		Assert.hasText(group, "The group permalink name has to be specified and cannot be empty.");

		if (mongoHost.contains(":")) {
			String[] parts = mongoHost.split(":");
			mongoClient = new MongoClient(parts[0], Integer.parseInt(parts[1]));
		} else {
			mongoClient = new MongoClient(mongoHost); // assuming standard port
		}
		
		log.info("Starting to import ravelry data from MongoDB database \"" + mongoDbName + "\" found at \""
				+ mongoHost + "\"");
		
		converterService.configure( mongoClient,	 discourseName,
				mongoHost,	 mongoDbName,	 dataSetName,  // Equals mongoDbName
				group	);
		converterService.addGroup();
		List<Integer> topics = converterService.addGroupsTopics();
		log.info("Done with topics " + topics.size());
		for (int topic : topics) {
			log.info("   Starting topic " + String.valueOf(topic));
			List<Integer> postings = converterService.addPostings(topic);
    		for (int posting : postings) {
    			String author = converterService.addUser(posting);
    		//	converterService.addPostingPatternRefs(posting);   No, should do this inside addPostings
    		//	converterService.addUsersPatterns(author);
    		}
		}
	}


}
