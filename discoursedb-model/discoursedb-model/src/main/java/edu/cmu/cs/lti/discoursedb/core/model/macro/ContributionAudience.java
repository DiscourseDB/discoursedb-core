package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.model.user.Audience;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="contribution_has_audience")
public class ContributionAudience implements Serializable{
	
	private static final long serialVersionUID = -2668707116929576568L;

	private long id;
	
    private Contribution contribution;
    
    private Audience audience;
    
    private Timestamp startTime;
    
    private Timestamp endTime;
	
    private Annotations annotations;
    
	public ContributionAudience() {}
    
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	@Id
	@Column(name="id_annotation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "id_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}


	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_contribution")
	public Contribution getContribution() {
		return contribution;
	}

	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_audience")
	public Audience getAudience() {
		return audience;
	}

	public void setAudience(Audience audience) {
		this.audience = audience;
	}
    
	
}
