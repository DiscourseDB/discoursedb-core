package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

/**
 * Adds basic fields to entities that keep track of their lifetime (Version,
 * entity creation date, start date, end date)
 * 
 * @author Oliver Ferschke
 *
 */
@MappedSuperclass
public abstract class TimedAnnotatableBaseEntity{

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
	
    private Date startTime;   
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

    private Date endTime;

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	private Annotations annotations;

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}



}
