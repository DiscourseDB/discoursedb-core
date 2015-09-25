package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="user_memberof_group", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_group", "fk_user" }))
public class GroupUser extends TimedAnnotatableBaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5232683398940704768L;

	private long id;
	
    private Group group;
    
    private User user;
    
	public GroupUser() {}
	
	@Id
	@Column(name="id_group_user", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@RestResource(rel="groupHasUser",path="groupHasUser")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_group")
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@RestResource(rel="userMemberOfGroup",path="userMemberOfGroup")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
	
}
