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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;

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
@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="context")
public class Context extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = 6013322457584994562L;

	private long id;
	
	private Content firstRevision;
	
	private Content currentRevision;
	
	private ContextType type;
	
	private Set<ContributionContext> contextContributions = new HashSet<ContributionContext>();

	public Context(){}

	@Id
	@Column(name="id_context", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_first_revision",insertable=false,updatable=false)
	public Content getFirstRevision() {
		return firstRevision;
	}

	public void setFirstRevision(Content firstRevision) {
		this.firstRevision = firstRevision;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_current_revision",insertable=false,updatable=false)
	public Content getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(Content currentRevision) {
		this.currentRevision = currentRevision;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_context_type")
	public ContextType getType() {
		return type;
	}

	public void setType(ContextType type) {
		this.type = type;
	}

    @OneToMany(mappedBy = "context")
	public Set<ContributionContext> getContextContributions() {
		return contextContributions;
	}

	public void setContextContributions(Set<ContributionContext> contextContributions) {
		this.contextContributions = contextContributions;
	}

}
