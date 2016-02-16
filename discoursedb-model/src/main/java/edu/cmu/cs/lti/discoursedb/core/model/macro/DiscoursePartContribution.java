package edu.cmu.cs.lti.discoursedb.core.model.macro;

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

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="contribution_partof_discourse_part", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_contribution", "fk_discourse_part" }))
public class DiscoursePartContribution extends TimedBaseEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_discourse_part_contribution", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution")
    private Contribution contribution;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_discourse_part")
    private DiscoursePart discoursePart;
	
}
