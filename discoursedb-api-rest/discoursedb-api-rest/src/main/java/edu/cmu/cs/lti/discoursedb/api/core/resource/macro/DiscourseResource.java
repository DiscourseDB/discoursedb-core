package edu.cmu.cs.lti.discoursedb.api.core.resource.macro;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;

public class DiscourseResource extends ResourceSupport {

	private long uid;
	private String name;
	private String descriptor;
	private Date lastModified;
	
	public DiscourseResource(Discourse discourse) {
		this.setName(discourse.getName());
		this.setDescriptor(discourse.getDescriptor());
		this.setLastModified(discourse.getVersion());
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

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}


}
