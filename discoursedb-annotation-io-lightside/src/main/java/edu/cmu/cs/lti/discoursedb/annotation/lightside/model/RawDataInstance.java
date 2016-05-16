package edu.cmu.cs.lti.discoursedb.annotation.lightside.model;

import java.util.Map;

import lombok.Data;

@Data
public class RawDataInstance {

	private boolean spanAnnotation;
	private String text;
	private Map<String, String> annotations;
		
}

