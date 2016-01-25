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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Context is whatever a Contribution is referring to. For example if the
 * contributions are comments on a Wikipedia talk page, then context might be
 * version of the wiki page at the point in time the comment was made. Context
 * entities are associated with Contribution entities via a ContributionContext
 * entity. Apart from that, Context entities resemble Contribution entities, ie.
 * the content of a Context entity is represented by one or more Content
 * instances.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="context")
public class Context extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = 6013322457584994562L;

	@Id
	@Column(name="id_context", nullable=false)
	@Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_first_revision")
	private Content firstRevision;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_current_revision")
	private Content currentRevision;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_context_type")
	private ContextType type;
	
    @OneToMany(mappedBy = "context")
	private Set<ContributionContext> contextContributions = new HashSet<ContributionContext>();

	public void addContextContributions(ContributionContext contextContribution) {
		this.contextContributions.add(contextContribution);
	}

}
