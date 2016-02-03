package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
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

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"discoursePartRelations"})
@ToString(callSuper=true, exclude={"discoursePartRelations"})
@Entity
@Table(name="discourse_part_relation_type")
public class DiscoursePartRelationType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = -3867055194845454430L;

	@Id
	@Column(name="id_discourse_part_relation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	private Set<DiscoursePartRelation> discoursePartRelations = new HashSet<DiscoursePartRelation>();
		
}
