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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "contribution_interaction", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "fk_user", "fk_contribution_interaction_type", "fk_contribution" }),
		@UniqueConstraint(columnNames = { "fk_user", "fk_contribution_interaction_type", "fk_content" }) })
public class ContributionInteraction extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = 3846201435729013318L;

	@Id
	@Column(name="id_contribution_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
	private User user;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_contribution")
	private Contribution contribution;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_content")
	private Content content;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_contribution_interaction_type")
	private ContributionInteractionType type;
	
}
