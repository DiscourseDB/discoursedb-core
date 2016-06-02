package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;

public class BrowsingDiscourseResource extends ResourceSupport {
	
	private String name;
	private Long discourseId;
	private Discourse d;
	private Map<String,Long> countsByType;
		
	public BrowsingDiscourseResource(Discourse d, DiscoursePartRepository dpr) {
		this.d = d;
		this.name = d.getName();
		this.discourseId = d.getId();
		List<Object[]> counts = dpr.countsByTypeAndDiscourseNative(d.getId()); 
		this.countsByType = new HashMap<>();
		counts.forEach(c  -> {
			String typename = c[0].toString();
			this.countsByType.put(typename, ((BigInteger)c[1]).longValue());
			this.add(BrowsingRestController.makeLink("/browsing/discourses/" + d.getId() + "/discoursePartTypes/" + typename, typename));
		});

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDiscourseId() {
		return discourseId;
	}

	public void setDiscourseId(Long discourseId) {
		this.discourseId = discourseId;
	}

	public Map<String, Long> getCountsByType() {
		return countsByType;
	}

	public void setCountsByType(Map<String, Long> countsByType) {
		this.countsByType = countsByType;
	}

	
	
}
