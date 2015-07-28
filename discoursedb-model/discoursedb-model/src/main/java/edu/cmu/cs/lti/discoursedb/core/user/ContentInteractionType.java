package edu.cmu.cs.lti.discoursedb.core.user;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="content_interaction_type")
public class ContentInteractionType implements Serializable{

	private static final long serialVersionUID = 4718842646037560198L;

	private long id;
	
	private String type;
	
	private Set<ContentInteraction> contentInteractions = new HashSet<ContentInteraction>();

	@Id
	@Column(name="id_content_interaction_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public ContentInteractionType(){}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<ContentInteraction> getContentInteractions() {
		return contentInteractions;
	}

	public void setContentInteractions(Set<ContentInteraction> contentInteractions) {
		this.contentInteractions = contentInteractions;
	}
}
