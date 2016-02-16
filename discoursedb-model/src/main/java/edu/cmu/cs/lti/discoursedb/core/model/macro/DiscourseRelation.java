package edu.cmu.cs.lti.discoursedb.core.model.macro;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="discourse_relation")
public class DiscourseRelation extends TimedAnnotatableBaseEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_discourse_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	private Contribution source;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	private Contribution target;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_relation_type")
	private DiscourseRelationType type;
	
}
