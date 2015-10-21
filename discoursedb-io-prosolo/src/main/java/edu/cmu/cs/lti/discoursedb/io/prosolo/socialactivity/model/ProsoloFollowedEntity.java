package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

/**
 * Wraps entities form the followed_entity table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
public class ProsoloFollowedEntity {
	private String dtype;	
	private Long id;
	private Date created;
	private Boolean deleted;
	private String dc_description;
	private String title;
	private Date started_following;
	private Long user;
	private Long followed_node;
	private Long followed_user;
	
	public ProsoloFollowedEntity(String dtype, Long id, Date created, Boolean deleted, String dc_description,
			String title, Date started_following, Long user, Long followed_node, Long followed_user) {
		super();
		this.dtype = dtype;
		this.id = id;
		this.created = created;
		this.deleted = deleted;
		this.dc_description = dc_description;
		this.title = title;
		this.started_following = started_following;
		this.user = user;
		this.followed_node = followed_node;
		this.followed_user = followed_user;
	}
	
	public String getDtype() {
		return dtype;
	}
	public void setDtype(String dtype) {
		this.dtype = dtype;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	public String getDc_description() {
		return dc_description;
	}
	public void setDc_description(String dc_description) {
		this.dc_description = dc_description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStarted_following() {
		return started_following;
	}
	public void setStarted_following(Date started_following) {
		this.started_following = started_following;
	}
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}
	public Long getFollowed_node() {
		return followed_node;
	}
	public void setFollowed_node(Long followed_node) {
		this.followed_node = followed_node;
	}
	public Long getFollowed_user() {
		return followed_user;
	}
	public void setFollowed_user(Long followed_user) {
		this.followed_user = followed_user;
	}
	
	
	
}
