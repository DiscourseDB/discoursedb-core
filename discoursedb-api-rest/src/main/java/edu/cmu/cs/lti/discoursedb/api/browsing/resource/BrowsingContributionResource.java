package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.hateoas.ResourceSupport;
import com.fasterxml.jackson.annotation.JsonFormat;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;

public class BrowsingContributionResource extends ResourceSupport {
	private String type;
	private String content;
	private String title;
	private String contributor;
	private Map<Long,String> discourseParts;
	private Date startTime;
	// links to discourseParts
	private List<String> userInteractions;
	private List<BrowsingAnnotationResource> annotations;
	
	public BrowsingContributionResource(Contribution c) {
		type = c.getType();
		content = c.getCurrentRevision().getText();
		title = c.getCurrentRevision().getTitle();
		startTime = c.getStartTime();
		setContributor(c.getCurrentRevision().getAuthor().getUsername());
		userInteractions = c.getContributionInteractions().stream().map(i -> 
			i.getUser().getUsername() + ": " + i.getType() + " at " + i.getStartTime().toString())
				.collect(Collectors.toList());
		annotations = new ArrayList<BrowsingAnnotationResource>();
		try {
			annotations.addAll(c.getAnnotations().getAnnotations().stream().map(a ->
				new BrowsingAnnotationResource(a))
				.collect(Collectors.toList()));
		} catch (NullPointerException npe) {
			
		}
		try {
			annotations.addAll(c.getCurrentRevision().getAnnotations().getAnnotations().stream().map(a ->
				new BrowsingAnnotationResource(a))
				.collect(Collectors.toList()));
		} catch (NullPointerException npe) {
			
		}
   	    discourseParts = new HashMap<Long,String>();
   	    for (DiscoursePartContribution dpc : c.getContributionPartOfDiscourseParts()) {
   	    	discourseParts.put(dpc.getDiscoursePart().getId(), dpc.getDiscoursePart().getName());
   	    }

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartTime() {
		return startTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	

	public List<BrowsingAnnotationResource> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BrowsingAnnotationResource> annotations) {
		this.annotations = annotations;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public List<String> getDiscourseParts() {
		return discourseParts.values().stream().collect(Collectors.toList());
	}
	public Map<Long,String> _getDiscourseParts() {
		return discourseParts;
	}


}
