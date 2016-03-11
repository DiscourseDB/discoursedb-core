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

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="user_memberof_group", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_group", "fk_user" }))
public class GroupUser extends TypedTimedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_group_user", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="groupHasUsers",path="groupHasUsers")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_group")
    private Group group;
    
	@RestResource(rel="userMemberOfGroups",path="userMemberOfGroups")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
    private User user;
    	
}
