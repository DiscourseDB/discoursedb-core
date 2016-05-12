package edu.cmu.cs.lti.discoursedb.annotation.lightside.model;

import java.util.List;

import org.jboss.jandex.AnnotationInstance;

import lombok.Data;

@Data
public class LightSideData {

	String text;
	List<AnnotationInstance> annos;
	
	
}
