package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.util.Date;
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

import edu.cmu.cs.lti.discoursedb.core.model.CoreBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part")
public class DiscoursePart extends CoreBaseEntity implements Serializable{

	private static final long serialVersionUID = -7341483666466458901L;

	private long id;
	
	private String name;
	
	private Date startTime;

	private Date endTime;
	
	private Annotations annotations;
	
	private DiscoursePartType type;
	
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

	private Set<DiscoursePartContribution> discoursePartContributions = new HashSet<DiscoursePartContribution>();
	
	private Set<DiscoursePartRelation> sourceOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();

	private Set<DiscoursePartRelation> targetOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();
	
	public DiscoursePart(){}

	private String sourceId;
	@Column(name="source_id")
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	@Id
	@Column(name="id_discourse_part", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part_type")
	public DiscoursePartType getType() {
		return type;
	}

	public void setType(DiscoursePartType type) {
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
	
    @OneToMany(mappedBy = "discoursePart")
	public Set<DiscourseToDiscoursePart> getDiscourseToDiscourseParts() {
		return discourseToDiscourseParts;
	}

    public void setDiscourseToDiscourseParts(Set<DiscourseToDiscoursePart> discourseToDiscourseParts) {
		this.discourseToDiscourseParts = discourseToDiscourseParts;
	}

    @OneToMany(mappedBy = "discoursePart")
	public Set<DiscoursePartContribution> getDiscoursePartContributions() {
		return discoursePartContributions;
	}

	public void setDiscoursePartContributions(Set<DiscoursePartContribution> discoursePartContributions) {
		this.discoursePartContributions = discoursePartContributions;
	}

	public void addDiscoursePartContribution(DiscoursePartContribution discoursePartContribution) {
		discoursePartContributions.add(discoursePartContribution);
	}

	
    @OneToMany(mappedBy="source")
	public Set<DiscoursePartRelation> getSourceOfDiscoursePartRelations() {
		return sourceOfDiscoursePartRelations;
	}

	public void setSourceOfDiscoursePartRelations(Set<DiscoursePartRelation> sourceOfDiscoursePartRelations) {
		this.sourceOfDiscoursePartRelations = sourceOfDiscoursePartRelations;
	}

    @OneToMany(mappedBy="target")
	public Set<DiscoursePartRelation> getTargetOfDiscoursePartRelations() {
		return targetOfDiscoursePartRelations;
	}

	public void setTargetOfDiscoursePartRelations(Set<DiscoursePartRelation> targetOfDiscoursePartRelations) {
		this.targetOfDiscoursePartRelations = targetOfDiscoursePartRelations;
	}

	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
}
