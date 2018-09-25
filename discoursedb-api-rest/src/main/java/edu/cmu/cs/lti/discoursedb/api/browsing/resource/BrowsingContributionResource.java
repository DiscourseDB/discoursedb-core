/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;

public class BrowsingContributionResource extends ResourceSupport {
	private Long id;
	private String type;
	private String content;
	private String title;
	private String contributor;
	private Map<Long,String> discourseParts;
	private Date startTime;
	// links to discourseParts
	private List<String> userInteractions;
	private List<BrowsingAnnotationResource> annotations;
	private Long parentId;
	private Contribution c;
	
	//@Autowired ContributionService contributionService;
	private static final Logger logger = LogManager.getLogger(BrowsingContributionResource.class);

	public BrowsingContributionResource(Contribution c, AnnotationService annoService, DiscoursePartService dps) {
	
		type = c.getType();
		id = c.getId();
		/*Contribution parent = contributionService.getOneRelatedContribution(c);
		if (parent == null) {
			parentId = 0L;
		} else {
			parentId = parent.getId();
		}*/
		if (c.getSourceOfDiscourseRelations().size() > 0) {
			parentId = c.getSourceOfDiscourseRelations().iterator().next().getTarget().getId();
		} else { 
			parentId = 0L;
		}
		content = c.getCurrentRevision().getText();
		title = c.getCurrentRevision().getTitle();
		startTime = c.getStartTime();
		setContributor(c.getCurrentRevision().getAuthor().getUsername());
		userInteractions = c.getContributionInteractions().stream().map(i -> 
			i.getUser().getUsername() + ": " + i.getType() + " at " + i.getStartTime().toString())
				.collect(Collectors.toList());
		annotations = new ArrayList<BrowsingAnnotationResource>();
		try {
			annotations.addAll(annoService.findAnnotations(c).stream().map(a ->
				new BrowsingAnnotationResource(a))
				.collect(Collectors.toList()));
			
		} catch (NullPointerException npe) {
			
		}
		try {
			annotations.addAll(annoService.findAnnotations(c.getCurrentRevision()).stream().map(a ->
				new BrowsingAnnotationResource(a))
				.collect(Collectors.toList()));
			
		} catch (NullPointerException npe) {
			
			
		}
   	    discourseParts = new HashMap<Long,String>();
   	    for (DiscoursePartContribution dpc : c.getContributionPartOfDiscourseParts()) {
   	    		discourseParts.put(dpc.getDiscoursePart().getId(), dps.fullyQualifiedName(dpc.getDiscoursePart()));
   	    }
   	    
	}

	public Long getContributionId() {
		return id;
	}

	public void setContributionId(Long id) {
		this.id = id;
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
	public List<Long> getDiscoursePartIds() {
		return discourseParts.keySet().stream().collect(Collectors.toList());
	}
	public Map<Long,String> _getDiscoursePartIds() {
		return discourseParts;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	
}
