package edu.cmu.cs.lti.discoursedb.core.model.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="user_relation")
public class UserRelation extends TypedTimedAnnotatableBE implements Identifiable<Long> {

	@Id
	@Column(name="id_user_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="sourceUser",path="sourceUser")
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	private User source;
	
	@RestResource(rel="targetUser",path="targetUser")
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	private User target;
}
