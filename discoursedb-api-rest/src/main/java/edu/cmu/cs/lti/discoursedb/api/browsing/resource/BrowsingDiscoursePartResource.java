package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.DiscoursePartInteraction;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.DiscoursePartInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;

public class BrowsingDiscoursePartResource extends ResourceSupport {
	
	private String name;
	private String type;
	private long subDiscoursePartCount;
	private long contributionCount;
	private List<String> userInteractions;
	private Date startTime;
	private Date endTime;
	private List<String> containingDiscourseParts;
	private DiscoursePart dp;
	private List<BrowsingAnnotationResource> annotations;
	
	private static final Logger logger = LogManager.getLogger(BrowsingDiscoursePartResource.class);	

	public BrowsingDiscoursePartResource(DiscoursePart dp) {
		this.dp = dp;
		this.setName(dp.getName());
		this.setType(dp.getType());
		this.setStartTime(dp.getStartTime());
		this.setEndTime(dp.getEndTime());
		
		if (dp.getAnnotations() != null) {
			List annos = new LinkedList<BrowsingAnnotationResource>();
			for (AnnotationInstance ai: dp.getAnnotations().getAnnotations()) {
				annos.add(new BrowsingAnnotationResource(ai));
			}
			this.setAnnotations(annos);
		}
		
		this.setContributionCount(dp.getDiscoursePartContributions().stream().collect(Collectors.summingLong(f -> 1L)));
		this.setSubDiscoursePartCount(dp.getSourceOfDiscoursePartRelations().stream().collect(Collectors.summingLong(f -> 1L)));
		
		

		if (this.getSubDiscoursePartCount() > 0) {
			this.add(BrowsingRestController.makeLink("/browsing/subDiscourseParts/" + dp.getId() + "/", 
					 dp.getName() ));			
		}
/*		for (DiscoursePartRelation dp1 : dp.getSourceOfDiscoursePartRelations()) {
			this.add(BrowsingRestController.makeLink("/browsing/subDiscourseParts/" + dp1.getTarget().getId() + "/", 
					dp1.getType() + ": " + dp1.getTarget().getName() ));			
		} */
		
		if (this.getContributionCount() > 0) {
			this.add(BrowsingRestController.makeLink("/browsing/dpContributions/" + dp.getId() + "/", "contributions"));
		}
		
   	    containingDiscourseParts = dp.getTargetOfDiscoursePartRelations().stream().map(dpr -> dpr.getSource().getName()).collect(Collectors.toList());
	}
	
	public void fillInUserInteractions(DiscoursePartInteractionRepository dpr) {
		for (DiscoursePartInteraction dpi : dpr.findAllByDiscoursePart(dp)) {
			String interaction = dpi.getUser().getUsername() + ": " + dpi.getType();
			if (dpi.getAnnotations() != null) {
				List<BrowsingAnnotationResource> anno = dpi.getAnnotations().getAnnotations().stream().
						map(BrowsingAnnotationResource::new).
						collect(Collectors.toList());
				if (annotations == null) { 
					annotations = new LinkedList<BrowsingAnnotationResource>();
				}
				annotations.addAll(anno);
			}
			if (userInteractions == null) {
				userInteractions = new LinkedList<String>();
			}
			userInteractions.add(interaction);
		}
	}
	
	public void filterAnnotations(String annotType) {
		if (!annotType.equals("*")) {
			setAnnotations(this.getAnnotations().stream().filter( bai -> bai.getType().equals(annotType) ).collect(Collectors.toList()));
		}
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


	public List<String> getContainingDiscourseParts() {
		return containingDiscourseParts;
	}

	public void setContainingDiscourseParts(List<String> containingDiscourseParts) {
		this.containingDiscourseParts = containingDiscourseParts;
	}



	public Date getStartTime() {
		return startTime;
	}


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}


	public Date getEndTime() {
		return endTime;
	}


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	public List<BrowsingAnnotationResource> getAnnotations() {
		return annotations;
	}


	public void setAnnotations(List<BrowsingAnnotationResource> annotations) {
		this.annotations = annotations;
	}

	public long getSubDiscoursePartCount() {
		return subDiscoursePartCount;
	}

	public void setSubDiscoursePartCount(long subDiscoursePartCount) {
		this.subDiscoursePartCount = subDiscoursePartCount;
	}

	public long getContributionCount() {
		return contributionCount;
	}

	public void setContributionCount(long contributionCount) {
		this.contributionCount = contributionCount;
	}

	public List<String> getUserInteractions() {
		return userInteractions;
	}

	public void setUserInteractions(List<String> userInteractions) {
		this.userInteractions = userInteractions;
	}


	
	

}
