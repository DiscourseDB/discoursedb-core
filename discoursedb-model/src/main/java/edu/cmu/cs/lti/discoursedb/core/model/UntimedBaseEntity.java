package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Adds basic fields to entities that do not keep track of their lifetime (version, entity creation date)
 * 
 * @author oliverf
 *
 */
@MappedSuperclass
public abstract class UntimedBaseEntity{

	private Long version;	
	@Version
	public Long getVersion() {
		return version;
	}
	@SuppressWarnings("unused")
	private void setVersion(Long version) {
		this.version = version;
	}
	
	private Date createDate;
	@CreationTimestamp
	@Column(name = "created")
	public Date getCreateDate() {
	    return this.createDate;
	}
	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setCreateDate(Date createDate) {
	    this.createDate = createDate;
	}
}
