package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;

public class RecommendationDiscourseResource extends ResourceSupport {
	
	private String name;
	private String descriptor;
	
	public RecommendationDiscourseResource(Discourse discourse) {
		this.setName(discourse.getName());
		this.setDescriptor(discourse.getDescriptor());
		this.add(linkTo(methodOn(RecommendationRestController.class).discourseToDiscourseParts(discourse.getId())).withRel("discourseParts"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}


}
