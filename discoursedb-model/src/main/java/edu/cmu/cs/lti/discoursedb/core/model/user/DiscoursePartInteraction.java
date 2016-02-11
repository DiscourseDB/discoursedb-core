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

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="discourse_part_interaction")
public class DiscoursePartInteraction extends TimedAnnotatableBaseEntity implements Serializable{

	private static final long serialVersionUID = -7782010595781927999L;

	@Id
	@Column(name="id_content_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
	private User user;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part")
	private DiscoursePart discoursePart;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part_interaction_type")
	private DiscoursePartInteractionType type;
	
}
