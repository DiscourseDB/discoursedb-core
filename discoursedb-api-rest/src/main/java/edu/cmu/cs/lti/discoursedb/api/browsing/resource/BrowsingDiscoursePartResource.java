package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;

public class BrowsingDiscoursePartResource extends ResourceSupport {
	
	private String name;
	private String type;
	private long subDiscoursePartCount;
	private long contributionCount;
	private Date startTime;
	private Date endTime;
	private List<BrowsingAnnotationResource> annotations;
	
	private static final Logger logger = LogManager.getLogger(BrowsingDiscoursePartResource.class);	

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
			this.setAnnotations(annos);
		}
		
		this.setContributionCount(dp.getDiscoursePartContributions().stream().collect(Collectors.summingLong(f -> 1L)));
		this.setSubDiscoursePartCount(dp.getTargetOfDiscoursePartRelations().stream().collect(Collectors.summingLong(f -> 1L)));
		
		for (DiscoursePartRelation dp1 : dp.getSourceOfDiscoursePartRelations()) {
			this.add(BrowsingRestController.makeLink("/browsing/subDiscourseParts/" + dp1.getTarget().getId() + "/", 
					dp1.getType() + ": " + dp1.getTarget().getName() ));			
		}
		
		this.add(BrowsingRestController.makeLink("/browsing/dpContributions/" + dp.getId() + "/", "contributions"));
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


	
	

}
