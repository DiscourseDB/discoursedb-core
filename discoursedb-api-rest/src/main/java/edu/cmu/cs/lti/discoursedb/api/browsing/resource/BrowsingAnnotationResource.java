package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;

public class BrowsingAnnotationResource extends ResourceSupport {
	private String type;
	private String range = "";
	private List<BrowsingFeatureResource> features;
	
	
	public BrowsingAnnotationResource(AnnotationInstance ai) {
		this.setType(ai.getType());
		if (ai.getBeginOffset() == ai.getEndOffset() && ai.getEndOffset() == 0) {
			this.setRange("(all)");
		} else {
			this.setRange(ai.getBeginOffset() + "-" + ai.getEndOffset());
		}
		this.setFeatures(ai.getFeatures().stream().map(f -> new BrowsingFeatureResource(f)).collect(Collectors.toList()));
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


	public String getRange() {
		return range;
	}


	public void setRange(String range) {
		this.range = range;
	}

}
