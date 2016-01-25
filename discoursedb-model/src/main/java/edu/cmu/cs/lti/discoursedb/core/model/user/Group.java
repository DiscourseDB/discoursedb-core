package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="\"group\"")
public class Group extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -8400689664755883198L;

	@Id
	@Column(name="id_group", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_group_type")
	private GroupType type;

	private String name;
	
    @OneToMany(mappedBy = "group")
	private Set<AudienceGroup> groupAudiences = new HashSet<AudienceGroup>();

    @OneToMany(mappedBy = "group")
	private Set<GroupUser> groupMembers = new HashSet<GroupUser>();
	
}
