package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableSourcedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"groupAudiences","groupMembers"})
@ToString(callSuper=true, exclude={"groupAudiences","groupMembers"})
@Entity
@Table(name="\"group\"")
public class Group extends TypedTimedAnnotatableSourcedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_group", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;

	private String name;
	
    @OneToMany(mappedBy = "group")
	private Set<AudienceGroup> groupAudiences = new HashSet<AudienceGroup>();

    @OneToMany(mappedBy = "group")
	private Set<GroupUser> groupMembers = new HashSet<GroupUser>();
	
}
