package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;

@Entity
@Table(name="user_relation_type")
public class UserRelationType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = 3266414066287662012L;

	private long id;
	
	private Set<UserRelation> userRelations = new HashSet<UserRelation>();

	public UserRelationType(){}

	@Id
	@Column(name="id_user_relation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<UserRelation> getUserRelations() {
		return userRelations;
	}

	public void setUserRelations(Set<UserRelation> userRelations) {
		this.userRelations = userRelations;
	}

}
