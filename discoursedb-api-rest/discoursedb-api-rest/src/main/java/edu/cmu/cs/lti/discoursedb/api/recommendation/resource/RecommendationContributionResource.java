package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
public class RecommendationContributionResource extends ResourceSupport {
	
	private String content;
	private String username;
	private String userrealname;
	private String usermail;
	private String contributionSourceId;
	private String contributionType;
//	private String threadStarterId;
	private Date creationTime;
	private int upvotes;
	
	public RecommendationContributionResource(Contribution contrib) {
		this.setContent(contrib.getCurrentRevision().getText());		
		this.setUsername(contrib.getCurrentRevision().getAuthor().getUsername());
		this.setUsermail(contrib.getCurrentRevision().getAuthor().getEmail());
		this.setUserrealname(contrib.getCurrentRevision().getAuthor().getRealname());
		this.setContributionSourceId(contrib.getSourceId());
		this.setContributionType(contrib.getType().getType());
		this.setCreationTime(contrib.getStartTime());
		this.setUpvotes(contrib.getUpvotes());
//		this.setThreadStarterId(methodOn(RecommendationRestController.class).contribParent(contrib.getContributionSourceId()).getSourceId());
		if(getContributionType().equals(ContributionTypes.POST.name())){			
			this.add(linkTo(methodOn(RecommendationRestController.class).contribParent(contrib.getSourceId())).withRel("threadStarterContribution"));
		}
		
		this.add(linkTo(methodOn(RecommendationRestController.class).user(contrib.getCurrentRevision().getAuthor().getId())).withRel("author"));
	}

	public String getContributionSourceId() {
		return contributionSourceId;
	}


	public void setContributionSourceId(String contributionSourceId) {
		this.contributionSourceId = contributionSourceId;
	}


	public String getContributionType() {
		return contributionType;
	}


	public void setContributionType(String contributionType) {
		this.contributionType = contributionType;
	}


	public Date getCreationTime() {
		return creationTime;
	}


	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}



	public int getUpvotes() {
		return upvotes;
	}



	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}



	public String getUsermail() {
		return usermail;
	}



	public void setUsermail(String usermail) {
		this.usermail = usermail;
	}



	public String getUserrealname() {
		return userrealname;
	}



	public void setUserrealname(String userrealname) {
		this.userrealname = userrealname;
	}

//	public String getThreadStarterId() {
//		return threadStarterId;
//	}
//
//	public void setThreadStarterId(String threadStarterId) {
//		this.threadStarterId = threadStarterId;
//	}

}
