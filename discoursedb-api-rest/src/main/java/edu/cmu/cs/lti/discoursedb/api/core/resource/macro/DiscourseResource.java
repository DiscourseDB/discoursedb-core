package edu.cmu.cs.lti.discoursedb.api.core.resource.macro;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;

public class DiscourseResource extends ResourceSupport {

	private long uid;
	private String name;
	
	public DiscourseResource(Discourse discourse) {
		this.setName(discourse.getName());
		this.setUid(discourse.getId());	
	}

	
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
