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
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;

/**
 * <p>A Discourse represents the broad context of interactions that might come from
 * multiple datasets. For example, a Discourse could represent an installment of
 * an online course. All interactions in the context of this course -
 * independent from the source dataset - will be associated with this Discourse
 * instance. Another installment of the same course would be represented by a
 * new Discourse instance.</p> 
 * 
 * <p>A Discourse is associated to one or more
 * DiscoursePart instances which represent sub-spaces in the realm of the
 * Discourse. That is, an online course with a discussion forum and chat would
 * have two DiscoursePart instances associated with its Discourse instance which
 * represent these two discussion spaces.</p>
 * 
 * @author Oliver Ferschke
 *
 */
@Entity
@SelectBeforeUpdate
@DynamicUpdate
@DynamicInsert
@Table(name = "discourse")
public class Discourse extends UntimedBaseEntity implements Serializable {

	private static final long serialVersionUID = -3736157436274230022L;

	private long id;

	private String name;

	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

	private Set<User> users;

	Discourse() {}

	public Discourse(String name){
		this.setName(name);
	}
	
	@Id
	@Column(name = "id_discourse", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(updatable=false, unique=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "discourse")
	public Set<DiscourseToDiscoursePart> getDiscourseToDiscourseParts() {
		return discourseToDiscourseParts;
	}

	public void setDiscourseToDiscourseParts(Set<DiscourseToDiscoursePart> discourseToDiscourseParts) {
		this.discourseToDiscourseParts = discourseToDiscourseParts;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "discourses")
	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
