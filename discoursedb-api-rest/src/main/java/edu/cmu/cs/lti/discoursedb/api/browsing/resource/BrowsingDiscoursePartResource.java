package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
public class BrowsingDiscoursePartResource extends ResourceSupport {
	
	private String name;
	private String type;
	private Date startTime;
	private Date endTime;
	private List<BrowsingAnnotationResource> ais;
	
	
	public BrowsingDiscoursePartResource(DiscoursePart dp) {
		this.setName(dp.getName());
		this.setType(dp.getType());
		this.setStartTime(dp.getStartTime());
		this.setEndTime(dp.getEndTime());
		
		if (dp.getAnnotations() != null) {
			List annos = new LinkedList<BrowsingAnnotationResource>();
			for (AnnotationInstance ai: dp.getAnnotations().getAnnotations()) {
				annos.add(new BrowsingAnnotationResource(ai));
			}
			this.setAis(annos);
		}
		
		/*this.add(linkTo(methodOn(RecommendationRestController.class).sourcesForContribution(contrib.getId())).withRel("contributionSources"));
		if(getContributionType().equals(ContributionTypes.POST.name())||getContributionType().equals(ContributionTypes.GOAL_NOTE.name())||getContributionType().equals(ContributionTypes.NODE_COMMENT.name())){			
			this.add(linkTo(methodOn(RecommendationRestController.class).contribParent(contrib.getId())).withRel("parentContribution"));
		}		
		if(getContributionType().equals(ContributionTypes.POST.name())){			
			this.add(linkTo(methodOn(RecommendationRestController.class).threadStarter(contrib.getId())).withRel("threadStarter"));
		}		
		this.add(linkTo(methodOn(RecommendationRestController.class).user(contrib.getCurrentRevision().getAuthor().getId())).withRel("author"));
		*/
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	


	public Date getStartTime() {
		return startTime;
	}


	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public List<BrowsingAnnotationResource> getAis() {
		return ais;
	}


	public void setAis(List<BrowsingAnnotationResource> ais) {
		this.ais = ais;
	}


	
	

}
