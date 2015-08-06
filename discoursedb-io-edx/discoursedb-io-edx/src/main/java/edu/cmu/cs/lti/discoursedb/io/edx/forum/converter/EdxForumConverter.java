package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

public class EdxForumConverter {

	private static final Logger logger = LogManager.getLogger(EdxForumConverter.class);
	private static int postcount = 1;
	
	public static void main(String[] args) throws Exception{
		EdxForumConverter converter = new EdxForumConverter();
		if(args.length!=1){
			logger.error("Missing input file. Must provide input file as launch parameter.");
			System.exit(1);
		}
		String inFileName = args[0];
		
		File infile = new File(inFileName);
		if(!infile.exists()||!infile.isFile()||!infile.canRead()){
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);			
		}
		logger.trace("Starting forum conversion");
		converter.convert(inFileName);
	}

	/**
	 * Stream-reads the json input and binds each post in the dataset to an
	 * object that is passed on to the mapper.
	 * 
	 * @param filename of json file that contains forum data 
	 * @throws IOException in case the inFile could not be read
	 * @throws JsonParseException in case the json was malformed and couln't be parsed 
	 */
	private void convert(String inFile) throws JsonParseException, JsonProcessingException, IOException {
		final InputStream in = new FileInputStream(inFile);
		try {
			for (Iterator<Post> it = new ObjectMapper().readValues(new JsonFactory().createParser(in),Post.class); it.hasNext();){
				logger.debug("Retrieving post number "+postcount++);
				Post curPost = it.next();
				map(curPost);
			}
		} finally {
			in.close();
		}
	}
	
	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param p
	 */
	private void map(Post p){
		logger.trace("Mapping post "+p.getId());
//		System.out.println(p);
	}

}
