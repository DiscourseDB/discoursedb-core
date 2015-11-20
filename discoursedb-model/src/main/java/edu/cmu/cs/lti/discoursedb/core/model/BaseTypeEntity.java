package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

/**
 * Adds basic common fields for type entities (Version, CreationDate, Type identifier) 
 * 
 * @author Oliver Ferschke
 *
 */
@MappedSuperclass
public abstract class BaseTypeEntity{

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
	
	private String type;

	@Column(unique=true)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
