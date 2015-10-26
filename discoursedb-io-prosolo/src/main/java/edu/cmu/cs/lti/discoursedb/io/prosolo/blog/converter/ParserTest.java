package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogPost;

public class ParserTest {

	public static void main(String[] args) throws Exception {
		try(InputStream in = new FileInputStream("/usr0/home/oliverf/dalmoocdata/dalmooc_blog_data.json")) {
			List<ProsoloBlogPost> pit =(List<ProsoloBlogPost>)new ObjectMapper().readValues(new JsonFactory().createParser(in), new TypeReference<List<ProsoloBlogPost>>(){}).next();	
			pit.stream().forEach(p->System.out.println(p.getAuthor()));		
		}	
	}

}
