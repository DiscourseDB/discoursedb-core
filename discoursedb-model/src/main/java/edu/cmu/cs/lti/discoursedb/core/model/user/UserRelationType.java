package edu.cmu.cs.lti.discoursedb.core.model.user;

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
@EqualsAndHashCode(callSuper=true, exclude={"userRelations"})
@ToString(callSuper=true, exclude={"userRelations"})
@Entity
@Table(name="user_relation_type")
public class UserRelationType extends BaseTypeEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_user_relation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	private Set<UserRelation> userRelations = new HashSet<UserRelation>();

}
