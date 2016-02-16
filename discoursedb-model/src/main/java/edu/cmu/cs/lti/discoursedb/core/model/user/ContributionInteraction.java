package edu.cmu.cs.lti.discoursedb.core.model.user;

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

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Identifiable;

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
public class ContributionInteraction extends TimedAnnotatableBaseEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_contribution_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="userPerformedInteraction",path="userPerformedInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
	private User user;
	
	@RestResource(rel="contributionOfInteraction",path="contributionOfInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_contribution")
	private Contribution contribution;

	@RestResource(rel="contentOfInteraction",path="contentOfInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_content")
	private Content content;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_contribution_interaction_type")
	private ContributionInteractionType type;
	
}
