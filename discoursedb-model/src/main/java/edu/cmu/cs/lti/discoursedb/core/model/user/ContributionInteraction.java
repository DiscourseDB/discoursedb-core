package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;

@Entity
@Table(name = "contribution_interaction", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "fk_user", "fk_contribution_interaction_type", "fk_contribution" }),
		@UniqueConstraint(columnNames = { "fk_user", "fk_contribution_interaction_type", "fk_content" }) })
public class ContributionInteraction extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = 3846201435729013318L;

	private long id;
	
	private User user;
	
	private Contribution contribution;

	private Content content;
	
	private ContributionInteractionType type;
	
	public ContributionInteraction(){}
	
	@Id
	@Column(name="id_contribution_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	 @ManyToOne(cascade = CascadeType.ALL)
	 @JoinColumn(name = "fk_user")
	 public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_contribution")
	public Contribution getContribution() {
		return contribution;
	}

	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_contribution_interaction_type")
	public ContributionInteractionType getType() {
		return type;
	}

	public void setType(ContributionInteractionType type) {
		this.type = type;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_content")
	public Content getContent() {
		return content;
	}
	
	public void setContent(Content content) {
		this.content = content;
	}

}
