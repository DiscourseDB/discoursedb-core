package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Audience implements Serializable {

	private static final long serialVersionUID = -8464122652580037597L;

	private long id;
	
	private Audience_Type type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Audience(){}
}
