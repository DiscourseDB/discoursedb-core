package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_relation")
public class DiscourseRelation implements Serializable {

	private static final long serialVersionUID = -2533440012916999219L;

	@Id
	@Column(name="id_discourse_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private Contribution source;
	
	private Contribution target;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private DiscourseRelationType type;
	
	private Annotations annotations;
	
	public DiscourseRelation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Contribution getSource() {
		return source;
	}

	public void setSource(Contribution source) {
		this.source = source;
	}

	public Contribution getTarget() {
		return target;
	}

	public void setTarget(Contribution target) {
		this.target = target;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public DiscourseRelationType getType() {
		return type;
	}

	public void setType(DiscourseRelationType type) {
		this.type = type;
	}

	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

}
