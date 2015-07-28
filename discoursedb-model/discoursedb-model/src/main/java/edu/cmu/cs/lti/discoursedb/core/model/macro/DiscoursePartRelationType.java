package edu.cmu.cs.lti.discoursedb.core.model.macro;

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
@Table(name="discourse_part_relation_type")
public class DiscoursePartRelationType implements Serializable {

	private static final long serialVersionUID = -3867055194845454430L;

	private long id;
	
	private String type;
	
	private Set<DiscoursePartRelation> discoursePartRelations = new HashSet<DiscoursePartRelation>();
	
	public DiscoursePartRelationType(){}

	@Id
	@Column(name="id_discourse_part_relation_type", nullable=false)
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

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<DiscoursePartRelation> getDiscoursePartRelations() {
		return discoursePartRelations;
	}

	public void setDiscoursePartRelations(Set<DiscoursePartRelation> discoursePartRelations) {
		this.discoursePartRelations = discoursePartRelations;
	}
	
}
