package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"discourseParts"})
@ToString(callSuper=true, exclude={"discourseParts"})
@Entity
@Table(name="discourse_part_type")
public class DiscoursePartType extends BaseTypeEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_discourse_part_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
    private Set<DiscoursePart> discourseParts=new HashSet<DiscoursePart>();
}
