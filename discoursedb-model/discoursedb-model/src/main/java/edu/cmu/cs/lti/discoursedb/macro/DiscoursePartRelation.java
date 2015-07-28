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
@Table(name="discourse_part_relation")
public class DiscoursePartRelation implements Serializable {

	private static final long serialVersionUID = 1914547709687781470L;

	@Id
	@Column(name="id_discourse_part_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private DiscoursePart source;
	
	private DiscoursePart target;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private Annotations annotations;
	
	private DiscoursePartRelationType type;
	
	public DiscoursePartRelation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DiscoursePart getSource() {
		return source;
	}

	public void setSource(DiscoursePart source) {
		this.source = source;
	}

	public DiscoursePart getTarget() {
		return target;
	}

	public void setTarget(DiscoursePart target) {
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

	public DiscoursePartRelationType getType() {
		return type;
	}

	public void setType(DiscoursePartRelationType type) {
		this.type = type;
	}

	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}
	
}
