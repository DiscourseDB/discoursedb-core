package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
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
		this.setEmail(user.getEmail());
		this.setCountry(user.getCountry());
		this.add(linkTo(methodOn(RecommendationRestController.class).sourcesForUser(user.getId())).withRel("userSources"));
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = nullCheck(country);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = nullCheck(email);
	}

	public String getEdxId() {
		return edxId;
	}

	public void setEdxId(String edxId) {
		this.edxId = nullCheck(edxId);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = nullCheck(username);
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = nullCheck(realname);
	}
	
	private String nullCheck(String s){
		return s==null?"":s;
	}

}
