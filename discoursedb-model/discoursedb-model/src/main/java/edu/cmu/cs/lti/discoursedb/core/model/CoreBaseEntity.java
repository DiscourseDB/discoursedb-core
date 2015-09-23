package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public abstract class CoreBaseEntity {

	private Date createDate;
	private Date updateDate;
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

	@UpdateTimestamp
	@Column(name = "last_updated")
	public Date getUpdateDate() {
	    return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
	    this.updateDate = updateDate;
	}
}
