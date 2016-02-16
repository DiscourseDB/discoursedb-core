package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * A Contribution entity is a representation of a contribution in a discussion
 * space such as a forum post, chat message or equivalent discourse artifact.
 * Contributions only represent meta information about the contribution while
 * the actual content is represented by Content entities (see below). This
 * allows DiscourseDB to capture the revision history of a contribution.
 * Revisions are Content entities that link to their previous and next revision.
 * Thus, the revision history of a contribution is represented by a doubly
 * linked list of Content instances and the Contribution links to the head and
 * the tail of this list. If not revisions are maintained, both pointers link to
 * the same Content entity. A Contribution is a typed entity, i.e. it is
 * associated with a ContributionType indicating what the Contribution instance
 * represents, e.g. a {@link edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes#POST}.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true, exclude={"contributionPartOfDiscourseParts","contributionAudiences","contributionContexts","sourceOfDiscourseRelations","targetOfDiscourseRelations","contributionInteractions"})
@ToString(callSuper=true, exclude={"contributionPartOfDiscourseParts","contributionAudiences","contributionContexts","sourceOfDiscourseRelations","targetOfDiscourseRelations","contributionInteractions"})
@Entity
@Table(name="contribution")
@Description("A contribution.")
public class Contribution extends TimedAnnotatableBaseEntityWithSource implements Identifiable<Long>{

	@Id
	@Column(name="id_contribution", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE)
	@Description("The primary key.")
	private Long id;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_first_revision")
	@Description("The content entity that represents the first revision of this contribution entity.")
	private Content firstRevision;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_current_revision")
	@Description("The content entity that represents the most current revision of this contribution entity.")
	private Content currentRevision;
	
	@Column(name="upvotes")
	@Description("The number of upvotes for this contribution.")
	private int upvotes;

	@Column(name="downvotes")
	@Description("The number of downvotes for this contribution.")
	private int downvotes;

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_contribution_type")
	@Description("The type of this contribution.")
	private ContributionType type;
	
    @OneToMany(mappedBy = "contribution")
	@Description("A set of relations that associate this contribution with one or more DiscoursePart entities.")
	private Set<DiscoursePartContribution> contributionPartOfDiscourseParts = new HashSet<DiscoursePartContribution>();

    @OneToMany(mappedBy = "contribution")
	@Description("A set of relations that associate this contribution with one or more audience entities.")
	private Set<ContributionAudience> contributionAudiences = new HashSet<ContributionAudience>();
	
    @OneToMany(mappedBy = "contribution")
	@Description("A set of relations that associate this contribution with one or more context entities.")
	private Set<ContributionContext> contributionContexts = new HashSet<ContributionContext>();
	
    @OneToMany(mappedBy="source")
	@Description("A set of relations that associate this contribution with a another contribution. This set contains only those relations of which the present contribution is the source.")
	private Set<DiscourseRelation> sourceOfDiscourseRelations = new HashSet<DiscourseRelation>();

    @OneToMany(mappedBy="target")
	@Description("A set of relations that associate this contribution with a another contribution. This set contains only those relations of which the present contribution is the target.")
	private Set<DiscourseRelation> targetOfDiscourseRelations = new HashSet<DiscourseRelation>();
	
    @OneToMany(mappedBy = "contribution")
	@Description("A set of relations that associate this contribution with one or more users.")
	private Set<ContributionInteraction> contributionInteractions = new HashSet<ContributionInteraction>();

    public void addContributionPartOfDiscourseParts(DiscoursePartContribution discoursePartContribution){
    	contributionPartOfDiscourseParts.add(discoursePartContribution);
    }

	public void addContributionAudiences(ContributionAudience contributionAudience) {
		this.contributionAudiences.add(contributionAudience);
	}

	public void addContributionContexts(ContributionContext contributionContext) {
		this.contributionContexts.add(contributionContext);
	}

	public void addSourceOfDiscourseRelations(DiscourseRelation sourceOfDiscourseRelation) {
		this.sourceOfDiscourseRelations.add(sourceOfDiscourseRelation);
	}
	public void addTargetOfDiscourseRelations(DiscourseRelation targetOfDiscourseRelation) {
		this.targetOfDiscourseRelations.add(targetOfDiscourseRelation);
	}


    
}
