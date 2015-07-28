package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="content")
public class Content implements Serializable {

	private static final long serialVersionUID = -7686736688076492828L;

	private long id;
	
	private Content previousRevision;
	
	private Content nextRevision;
	
	private Timestamp creationTime;
	
	private String text;
	
	private Blob data;
	
	private Annotations annotations;
	
	public Content(){}

	@Id
	@Column(name="id_content", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name="previous_revision")
	public Content getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(Content previousRevision) {
		this.previousRevision = previousRevision;
	}

	@Column(name="next_revision")
	public Content getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(Content nextRevision) {
		this.nextRevision = nextRevision;
	}

	@Column(name="creation_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}
	
}
