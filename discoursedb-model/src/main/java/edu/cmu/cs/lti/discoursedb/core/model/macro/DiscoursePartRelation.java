package edu.cmu.cs.lti.discoursedb.core.model.macro;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="discourse_part_relation")
public class DiscoursePartRelation extends TypedTimedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_discourse_part_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	private DiscoursePart source;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	private DiscoursePart target;
	
}
