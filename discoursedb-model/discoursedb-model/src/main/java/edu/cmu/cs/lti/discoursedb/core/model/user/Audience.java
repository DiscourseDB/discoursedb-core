package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionAudience;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="audience")
public class Audience extends UntimedBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -8464122652580037597L;

	private long id;
	
	private AudienceType type;
	
	private Annotations annotations;
	
	private Set<ContributionAudience> audienceContributions = new HashSet<ContributionAudience>();
	
	private Set<AudienceUser> audienceUsers = new HashSet<AudienceUser>();

	private Set<AudienceGroup> audienceGroups = new HashSet<AudienceGroup>();
	
	public Audience(){}
	
	@Id
	@Column(name="id_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_audience_type")
	public AudienceType getType() {
		return type;
	}

	public void setType(AudienceType type) {
		this.type = type;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}


	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

    @OneToMany(mappedBy = "audience")
	public Set<ContributionAudience> getAudienceContributions() {
		return audienceContributions;
	}

	public void setAudienceContributions(Set<ContributionAudience> audienceContributions) {
		this.audienceContributions = audienceContributions;
	}

    @OneToMany(mappedBy = "audience")
	public Set<AudienceUser> getAudienceUsers() {
		return audienceUsers;
	}

	public void setAudienceUsers(Set<AudienceUser> audienceUsers) {
		this.audienceUsers = audienceUsers;
	}

    @OneToMany(mappedBy = "audience")
	public Set<AudienceGroup> getAudienceGroups() {
		return audienceGroups;
	}

	public void setAudienceGroups(Set<AudienceGroup> audienceGroups) {
		this.audienceGroups = audienceGroups;
	}

}
