package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;

public class BrowsingFeatureResource extends ResourceSupport {

	String type;
	String value;
	
	public BrowsingFeatureResource(Feature f) {
		setType(f.getType());
		setValue(f.getValue());
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
