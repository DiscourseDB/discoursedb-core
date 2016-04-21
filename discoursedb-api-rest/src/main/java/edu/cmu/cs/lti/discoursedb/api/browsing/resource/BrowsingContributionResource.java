package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;

public class BrowsingContributionResource extends ResourceSupport {
	private String type;
	private String content;
	private String title;
	private Date startTime;
	// links to discourseParts
	private List<String> userInteractions;
	private List<BrowsingAnnotationResource> annotations;
	
	public BrowsingContributionResource(Contribution c) {
		type = c.getType();
		content = c.getCurrentRevision().getText();
		title = c.getCurrentRevision().getTitle();
		startTime = c.getStartTime();
		userInteractions = c.getContributionInteractions().stream().map(i -> 
			i.getUser().getUsername() + ": " + i.getType() + " at " + i.getStartTime().toString())
				.collect(Collectors.toList());
		try {
			annotations = c.getAnnotations().getAnnotations().stream().map(a ->
				new BrowsingAnnotationResource(a))
				.collect(Collectors.toList());
		} catch (NullPointerException npe) {
			annotations = null;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public List<String> getUserInteractions() {
		return userInteractions;
	}

	public void setUserInteractions(List<String> userInteractions) {
		this.userInteractions = userInteractions;
	}

	public List<BrowsingAnnotationResource> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BrowsingAnnotationResource> annotations) {
		this.annotations = annotations;
	}



}
