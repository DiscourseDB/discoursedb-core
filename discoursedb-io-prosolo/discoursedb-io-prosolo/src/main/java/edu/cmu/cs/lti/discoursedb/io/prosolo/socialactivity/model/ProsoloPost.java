package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

public class ProsoloPost {
 private Long id;
 private Date created;
 private Boolean deleted;
 private String dc_description;
 private String title;
 private String content;
 private String link;
 private String visibility;
 private Boolean connect_with_status;
 private Long maker;
 private Long reshare_of;
 private Long rich_content;
 private Long goal;
 private String post_link;
 
public ProsoloPost(Long id, Date created, Boolean deleted, String dc_description, String title, String content,
		String link, String visibility, Boolean connect_with_status, Long maker, Long reshare_of, Long rich_content,
		Long goal, String post_link) {
	super();
	this.id = id;
	this.created = created;
	this.deleted = deleted;
	this.dc_description = dc_description;
	this.title = title;
	this.content = content;
	this.link = link;
	this.visibility = visibility;
	this.connect_with_status = connect_with_status;
	this.maker = maker;
	this.reshare_of = reshare_of;
	this.rich_content = rich_content;
	this.goal = goal;
	this.post_link = post_link;
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
public String getContent() {
	return content;
}
public void setContent(String content) {
	this.content = content;
}
public String getLink() {
	return link;
}
public void setLink(String link) {
	this.link = link;
}
public String getVisibility() {
	return visibility;
}
public void setVisibility(String visibility) {
	this.visibility = visibility;
}
public Boolean getConnect_with_status() {
	return connect_with_status;
}
public void setConnect_with_status(Boolean connect_with_status) {
	this.connect_with_status = connect_with_status;
}
public Long getMaker() {
	return maker;
}
public void setMaker(Long maker) {
	this.maker = maker;
}
public Long getReshare_of() {
	return reshare_of;
}
public void setReshare_of(Long reshare_of) {
	this.reshare_of = reshare_of;
}
public Long getRich_content() {
	return rich_content;
}
public void setRich_content(Long rich_content) {
	this.rich_content = rich_content;
}
public Long getGoal() {
	return goal;
}
public void setGoal(Long goal) {
	this.goal = goal;
}
public String getPost_link() {
	return post_link;
}
public void setPost_link(String post_link) {
	this.post_link = post_link;
}
 
 
}
