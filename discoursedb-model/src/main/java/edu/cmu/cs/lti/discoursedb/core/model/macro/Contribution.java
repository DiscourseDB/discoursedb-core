package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
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

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="contribution")
public class Contribution extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -2489956863731652149L;

	@Id
	@Column(name="id_contribution", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_first_revision")
	private Content firstRevision;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_current_revision")
	private Content currentRevision;
	
	@Column(name="upvotes")
	private int upvotes;

	@Column(name="downvotes")
	private int downvotes;

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_contribution_type")
	private ContributionType type;
	
    @OneToMany(mappedBy = "contribution")
	private Set<DiscoursePartContribution> contributionPartOfDiscourseParts = new HashSet<DiscoursePartContribution>();

    @OneToMany(mappedBy = "contribution")
	private Set<ContributionAudience> contributionAudiences = new HashSet<ContributionAudience>();
	
    @OneToMany(mappedBy = "contribution")
	private Set<ContributionContext> contributionContexts = new HashSet<ContributionContext>();
	
    @OneToMany(mappedBy="source")
	private Set<DiscourseRelation> sourceOfDiscourseRelations = new HashSet<DiscourseRelation>();

    @OneToMany(mappedBy="target")
	private Set<DiscourseRelation> targetOfDiscourseRelations = new HashSet<DiscourseRelation>();
	
    @OneToMany(mappedBy = "contribution")
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
