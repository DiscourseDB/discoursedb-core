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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper=true, exclude={"contextContributions"})
@ToString(callSuper=true, exclude={"contextContributions"})
@Entity
@Table(name="context")
@Description("The context of one or more contributions.")
public class Context extends TimedAnnotatableBaseEntityWithSource implements Identifiable<Long>{

	@Id
	@Column(name="id_context", nullable=false)
	@Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Description("The primary key.")
	private Long id;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_first_revision")
	@Description("The content entity that represents the first revision of this context entity.")
	private Content firstRevision;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, fetch=FetchType.LAZY) 
	@JoinColumn(name = "fk_current_revision")
	@Description("The content entity that represents the most current revision of this context entity.")
	private Content currentRevision;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_context_type")
	@Description("The type of this context.")
	private ContextType type;
	
    @OneToMany(mappedBy = "context")
	@Description("A set of relations that associate this content to contributions.")
	private Set<ContributionContext> contextContributions = new HashSet<ContributionContext>();

	public void addContextContributions(ContributionContext contextContribution) {
		this.contextContributions.add(contextContribution);
	}

}
