package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;

public class RecommendationDiscoursePartResource extends ResourceSupport {
	
	private String name;
	
	public RecommendationDiscoursePartResource(DiscoursePart discoursePart) {
		this.setName(discoursePart.getName());
		this.add(linkTo(methodOn(RecommendationRestController.class).contributionsForDiscoursePart(discoursePart.getId())).withRel("contributions"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
