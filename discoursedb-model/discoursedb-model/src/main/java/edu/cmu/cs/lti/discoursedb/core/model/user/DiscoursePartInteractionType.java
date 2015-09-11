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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part_interaction_type")
public class DiscoursePartInteractionType implements Serializable{

	private static final long serialVersionUID = -4161973201397061713L;

	private long id;
	
	private String type;
	
	private Set<DiscoursePartInteraction> discoursePartInteractions = new HashSet<DiscoursePartInteraction>();

	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}
	
	@Id
	@Column(name="id_discourse_part_interaction_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(unique=true)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public DiscoursePartInteractionType(){}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<DiscoursePartInteraction> getContentInteractions() {
		return discoursePartInteractions;
	}

	public void setContentInteractions(Set<DiscoursePartInteraction> discoursePartInteractions) {
		this.discoursePartInteractions = discoursePartInteractions;
	}
}
