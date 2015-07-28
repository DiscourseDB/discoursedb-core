package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="contribution")
public class Contribution implements Serializable {

	private static final long serialVersionUID = -2489956863731652149L;

	private long id;
	
	private Content firstRevision;
	
	private Content currentRevision;
	
	private Timestamp startTime;
	
	private Timestamp endTime;

	private ContributionType type;
	
	private Annotations annotations;
	
	private Set<DiscoursePartContribution> contributionPartOfDiscourseParts = new HashSet<DiscoursePartContribution>();

	private Set<ContributionAudience> contributionAudiences = new HashSet<ContributionAudience>();
	
	private Set<ContributionContext> contributionContexts = new HashSet<ContributionContext>();
	
	public Contribution(){}

	@Id
	@Column(name="id_contribution", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="first_revision")
	public Content getFirstRevision() {
		return firstRevision;
	}

	public void setFirstRevision(Content firstRevision) {
		this.firstRevision = firstRevision;
	}

	@Column(name="current_revision")
	public Content getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(Content currentRevision) {
		this.currentRevision = currentRevision;
	}

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

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "id_contribution_type")
	public ContributionType getType() {
		return type;
	}

	public void setType(ContributionType type) {
		this.type = type;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "id_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

    @OneToMany(mappedBy = "contribution")
	public Set<DiscoursePartContribution> getContributionPartOfDiscourseParts() {
		return contributionPartOfDiscourseParts;
	}

	public void setContributionPartOfDiscourseParts(Set<DiscoursePartContribution> contributionPartOfDiscourseParts) {
		this.contributionPartOfDiscourseParts = contributionPartOfDiscourseParts;
	}

    @OneToMany(mappedBy = "contribution")
	public Set<ContributionAudience> getContributionAudiences() {
		return contributionAudiences;
	}

	public void setContributionAudiences(Set<ContributionAudience> contributionAudiences) {
		this.contributionAudiences = contributionAudiences;
	}

    @OneToMany(mappedBy = "contribution")
	public Set<ContributionContext> getContributionContexts() {
		return contributionContexts;
	}

	public void setContributionContexts(Set<ContributionContext> contributionContexts) {
		this.contributionContexts = contributionContexts;
	}


}
