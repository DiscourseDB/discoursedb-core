package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public class RecommendationUserResource extends ResourceSupport {
	
	private String username;
	private String realname;
	private String email;
	private String edxId;
	private String country;
	
	public RecommendationUserResource(User user) {
		this.setUsername(user.getUsername());
		this.setRealname(user.getRealname());
		this.setEdxId(user.getSourceId());
		this.setEmail(user.getEmail());
		this.setCountry(user.getCountry());
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEdxId() {
		return edxId;
	}

	public void setEdxId(String edxId) {
		this.edxId = edxId;
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
