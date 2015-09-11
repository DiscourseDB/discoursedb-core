package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;

public class RecommendationContributionResource extends ResourceSupport {
	
	private String text;
	private String username;
	
	public RecommendationContributionResource(Contribution contrib) {
		this.setUsername(contrib.getCurrentRevision().getAuthor().getUsername());
		this.setText(contrib.getCurrentRevision().getText());		
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

}
