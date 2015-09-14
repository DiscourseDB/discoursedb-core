package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public class RecommendationUserResource extends ResourceSupport {
	
	private String username;
	private String realname;
	
	public RecommendationUserResource(User user) {
		this.setUsername(user.getUsername());
		this.setRealname(user.getRealname());
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

}
