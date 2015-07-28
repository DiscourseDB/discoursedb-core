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
@Table(name="discourse_part_type")
public class DiscoursePartType implements Serializable {

	private static final long serialVersionUID = 532051260123187170L;

	@Id
	@Column(name="id_discourse_part_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String type;
	
	public DiscoursePartType(){}

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
