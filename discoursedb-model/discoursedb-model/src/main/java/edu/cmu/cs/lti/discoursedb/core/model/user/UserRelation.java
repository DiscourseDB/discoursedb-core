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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="user_relation")
public class UserRelation extends TimedBaseEntity implements Serializable {

	private static final long serialVersionUID = -5267036520925282560L;

	private long id;
	
	private UserRelationType type;
	
	private User source;
	
	private User target;
	
	private Annotations annotations;
	
	public UserRelation(){}
	
	@Id
	@Column(name="id_user_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_user_relation_type")
	public UserRelationType getType() {
		return type;
	}

	public void setType(UserRelationType type) {
		this.type = type;
	}

	@RestResource(rel="sourceUser",path="sourceUser")
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	public User getSource() {
		return source;
	}

	public void setSource(User source) {
		this.source = source;
	}

	@RestResource(rel="targetUser",path="targetUser")
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}
	
	@RestResource(rel="userRelationsAnnotations",path="userRelationsAnnotations")
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}


	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

}
