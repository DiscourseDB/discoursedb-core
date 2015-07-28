package edu.cmu.cs.lti.discoursedb.macro;

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
@Table(name="discourse_relation_type")
public class DiscourseRelationType implements Serializable {

	private static final long serialVersionUID = -6905270877949246079L;

	@Id
	@Column(name="id_discourse_relation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String type;

	public DiscourseRelationType(){}
	
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

}
