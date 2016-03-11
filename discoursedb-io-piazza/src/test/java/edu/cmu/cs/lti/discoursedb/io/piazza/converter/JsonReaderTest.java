package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaContent;


public class JsonReaderTest {

	public static void main(String[] args) throws Exception{

		try(InputStream in = new FileInputStream(new File(args[0]))) {
			
			@SuppressWarnings("unchecked")
			List<PiazzaContent> contents =(List<PiazzaContent>)new ObjectMapper()
					.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
					.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
					.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
					.readValues(new JsonFactory().createParser(in), new TypeReference<List<PiazzaContent>>(){}).next();	
			contents.stream().forEach(c->System.out.println(c.getCreated()));
		}	
		
	}
}
