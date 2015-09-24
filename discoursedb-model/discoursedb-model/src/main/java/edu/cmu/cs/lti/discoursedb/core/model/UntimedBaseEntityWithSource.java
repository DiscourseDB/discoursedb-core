package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Adds source information to to regular untimed entities
 * 
 * @author Oliver Ferschke
 *
 */
@MappedSuperclass
public abstract class UntimedBaseEntityWithSource extends UntimedBaseEntity{

	private String sourceId;
	@Column(name="source_id")
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


}
