package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

public class EdxForumConverter {

	public static void main(String[] args) throws Exception{
		EdxForumConverter converter = new EdxForumConverter();
//		converter.convert(args[0]);
		converter.convert("/usr0/home/oliverf/dalmoocdata/forum.json");
	}

	private void convert(String inFile) throws Exception{
		final InputStream in = new FileInputStream(inFile);
		try {
			for (Iterator<Post> it = new ObjectMapper().readValues(new JsonFactory().createParser(in),Post.class); it.hasNext();){
				Post curPost = it.next();

				System.out.println(curPost);				
			}
		} finally {
			in.close();
		}
	}

}
