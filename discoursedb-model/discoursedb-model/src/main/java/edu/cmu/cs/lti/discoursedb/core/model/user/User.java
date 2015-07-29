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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="user")
public class User implements Serializable {

	private static final long serialVersionUID = 8065834868365920898L;

	private long id;
	
	private String realname;
	
	private String username;
	
	private String email;
	
	private String ip;
	
	private String language;
	
	private String location;

	private Annotations annotations;

	private Set<ContentInteraction> contentInteractions = new HashSet<ContentInteraction>();

	private Set<AudienceUser> userAudiences = new HashSet<AudienceUser>();
	
	private Set<GroupUser> userGroups = new HashSet<GroupUser>();
	
	private Set<UserRelation> sourceOfUserRelations = new HashSet<UserRelation>();

	private Set<UserRelation> targetOfUserRelations = new HashSet<UserRelation>();
	
	public User(){}

	@Id
	@Column(name="id_user", nullable=false)
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

	@ManyToOne(cascade=CascadeType.ALL) 
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

    @OneToMany(mappedBy="source")
	public Set<UserRelation> getSourceOfUserRelations() {
		return sourceOfUserRelations;
	}

	public void setSourceOfUserRelations(Set<UserRelation> sourceOfUserRelations) {
		this.sourceOfUserRelations = sourceOfUserRelations;
	}

    @OneToMany(mappedBy="target")
	public Set<UserRelation> getTargetOfUserRelations() {
		return targetOfUserRelations;
	}

	public void setTargetOfUserRelations(Set<UserRelation> targetOfUserRelations) {
		this.targetOfUserRelations = targetOfUserRelations;
	}

}
