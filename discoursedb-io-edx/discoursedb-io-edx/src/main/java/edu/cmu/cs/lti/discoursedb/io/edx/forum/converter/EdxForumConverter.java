package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

public class EdxForumConverter {
	
	
	public static void main(String[] args) throws Exception{
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		Post post = mapper.readValue(new File("src/main/resources/forum.json"), Post.class);
		System.out.println(post);
	}		

	
}
