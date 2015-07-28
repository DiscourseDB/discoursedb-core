package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="feature")
public class Feature implements Serializable{

	private static final long serialVersionUID = -5462337134833586687L;

	@Id
	@Column(name="id_feature", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String text;
	
	private FeatureType type;
	
	public Feature(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public FeatureType getType() {
		return type;
	}

	public void setType(FeatureType type) {
		this.type = type;
	}
	
}
