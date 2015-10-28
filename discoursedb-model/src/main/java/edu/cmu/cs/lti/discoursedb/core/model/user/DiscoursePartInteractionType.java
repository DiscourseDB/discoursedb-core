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
@Table(name="discourse_part_interaction_type")
public class DiscoursePartInteractionType extends BaseTypeEntity implements Serializable{

	private static final long serialVersionUID = -4161973201397061713L;

	private long id;
	
	private Set<DiscoursePartInteraction> discoursePartInteractions = new HashSet<DiscoursePartInteraction>();
	
	@Id
	@Column(name="id_discourse_part_interaction_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
