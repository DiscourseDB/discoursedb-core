package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;

public class BrowsingAnnotationResource extends ResourceSupport {
	private String type;
	private List<BrowsingFeatureResource> features;
	
	
	public BrowsingAnnotationResource(AnnotationInstance ai) {
		this.setType(ai.getType());
		List feats = new LinkedList<BrowsingFeatureResource>();
		for (Feature f: ai.getFeatures()) {
			feats.add(new BrowsingFeatureResource(f));
		}
		this.setFeatures(feats);
	}


	public List<BrowsingFeatureResource> getFeatures() {
		return features;
	}


	public void setFeatures(List<BrowsingFeatureResource> features) {
		this.features = features;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

}
