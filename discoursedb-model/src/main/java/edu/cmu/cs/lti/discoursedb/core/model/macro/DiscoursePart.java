package edu.cmu.cs.lti.discoursedb.core.model.macro;

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

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;

/**
 * A DiscoursePart represents a distinct sub-space within a Discourse. For
 * instance, a DiscoursePart could represent a discussion forum. That is, it
 * acts as a container for interactions that happen in this discussion forum.
 * DiscourseParts are typed entities, i.e. they are associated with a
 * DiscoursePartType which indicates what the DiscoursePart represents, e.g. a
 * {@link edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes#FORUM}. 
 * 
 * Furthermore, DiscourseParts can be related to each other with DiscoursePartRelations in order to indicate embedded structures. 
 * For instance, a forum could consist of multiple sub-forums. 
 * 
 * DiscoursePartRelations are also typed entities, i.e. they are related to a DiscoursePartRelationType indicating what the relation represents, 
 * e.g. an EMBEDDING in the case of forum-subforum.
 * 
 * @author Oliver Ferschke
 *
 */
@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part")
public class DiscoursePart extends TimedAnnotatableBaseEntityWithSource implements Serializable{

	private static final long serialVersionUID = -7341483666466458901L;

	private long id;
	
	private String name;
	
	private DiscoursePartType type;
	
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

	private Set<DiscoursePartContribution> discoursePartContributions = new HashSet<DiscoursePartContribution>();
	
	private Set<DiscoursePartRelation> sourceOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();

	private Set<DiscoursePartRelation> targetOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();
	
	public DiscoursePart(){}
	
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
	
}
