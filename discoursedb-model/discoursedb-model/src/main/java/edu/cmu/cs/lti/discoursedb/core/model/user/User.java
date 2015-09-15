package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;

/**
 * TODO: We should have an insert trigger that assures that a User is associated with a discourse
 * 
 * @author Oliver Ferschke
 *
 */
@Entity
@SelectBeforeUpdate
@DynamicUpdate
@DynamicInsert
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "source_id", "username" }) )
public class User implements Serializable {

	private static final long serialVersionUID = 8065834868365920898L;

	private long id;

	private String realname;

	private String username;

	private String email;

	private String ip;

	private String language;

	private String country;

	private String location;

	private Set<Discourse> discourses = new HashSet<Discourse>();

	private Annotations annotations;

	private Set<ContentInteraction> contentInteractions = new HashSet<ContentInteraction>();

	private Set<AudienceUser> userAudiences = new HashSet<AudienceUser>();

	private Set<GroupUser> userGroups = new HashSet<GroupUser>();

	private Set<UserRelation> sourceOfUserRelations = new HashSet<UserRelation>();

	private Set<UserRelation> targetOfUserRelations = new HashSet<UserRelation>();

	private String sourceId;
	@Column(name="source_id")
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	
	public User() {}

	private Date version;

	@Version
	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	@Id
	@Column(name = "id_user", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@RestResource(rel="userAnnotations",path="userAnnotations")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

	@OneToMany(mappedBy = "user")
	public Set<ContentInteraction> getContentInteractions() {
		return contentInteractions;
	}

	public void setContentInteractions(Set<ContentInteraction> contentInteractions) {
		this.contentInteractions = contentInteractions;
	}

	@OneToMany(mappedBy = "user")
	public Set<AudienceUser> getUserAudiences() {
		return userAudiences;
	}

	public void setUserAudiences(Set<AudienceUser> userAudiences) {
		this.userAudiences = userAudiences;
	}

	@OneToMany(mappedBy = "user")
	public Set<GroupUser> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(Set<GroupUser> userGroups) {
		this.userGroups = userGroups;
	}

	@OneToMany(mappedBy = "source")
	public Set<UserRelation> getSourceOfUserRelations() {
		return sourceOfUserRelations;
	}

	public void setSourceOfUserRelations(Set<UserRelation> sourceOfUserRelations) {
		this.sourceOfUserRelations = sourceOfUserRelations;
	}

	@OneToMany(mappedBy = "target")
	public Set<UserRelation> getTargetOfUserRelations() {
		return targetOfUserRelations;
	}

	public void setTargetOfUserRelations(Set<UserRelation> targetOfUserRelations) {
		this.targetOfUserRelations = targetOfUserRelations;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "discourse_has_user", joinColumns = {
		@JoinColumn(name = "id_user", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "id_discourse", nullable = false, updatable = false) })
	public Set<Discourse> getDiscourses() {
		return discourses;
	}

	public void setDiscourses(Set<Discourse> discourses) {
		this.discourses = discourses;
	}

	public void addDiscourses(Discourse discourse) {
		this.discourses.add(discourse);
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	
}
