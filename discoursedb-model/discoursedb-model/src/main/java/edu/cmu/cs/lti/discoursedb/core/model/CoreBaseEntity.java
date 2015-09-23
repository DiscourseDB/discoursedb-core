package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

@MappedSuperclass
public abstract class CoreBaseEntity {

	private Date createDate;
	private Long version;
	
	@Version
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	
	@CreationTimestamp
	@Column(name = "created")
	public Date getCreateDate() {
	    return this.createDate;
	}

	public void setCreateDate(Date createDate) {
	    this.createDate = createDate;
	}

}
