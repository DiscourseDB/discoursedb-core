package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;
import edu.cmu.cs.lti.discoursedb.macro.Content;

public class ContentInteraction implements Serializable{

	private static final long serialVersionUID = 3846201435729013318L;

	private long id;
	
	private User user;
	
	private Content content;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private ContentInteractionType type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public ContentInteraction(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEnd_time(Timestamp endTime) {
		this.endTime = endTime;
	}

	public ContentInteractionType getType() {
		return type;
	}

	public void setType(ContentInteractionType type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
