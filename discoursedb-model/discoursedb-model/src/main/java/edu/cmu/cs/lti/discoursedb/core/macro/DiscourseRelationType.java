package edu.cmu.cs.lti.discoursedb.core.macro;

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
@Table(name="discourse_relation_type")
public class DiscourseRelationType implements Serializable {

	private static final long serialVersionUID = -6905270877949246079L;

	private long id;
	
	private String type;
	
	private Set<DiscourseRelation> discourseRelations = new HashSet<DiscourseRelation>();

	public DiscourseRelationType(){}
	
	@Id
	@Column(name="id_discourse_relation_type", nullable=false)
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
	public Set<DiscourseRelation> getDiscourseRelations() {
		return discourseRelations;
	}

	public void setDiscourseRelations(Set<DiscourseRelation> discourseRelations) {
		this.discourseRelations = discourseRelations;
	}

}
