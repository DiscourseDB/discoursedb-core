package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.macro.Content;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="content_interaction")
public class ContentInteraction implements Serializable{

	private static final long serialVersionUID = 3846201435729013318L;

	private long id;
	
	private User user;
	
	private Content content;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private ContentInteractionType type;
	
	private Annotations annotations;
	
	public ContentInteraction(){}

	@Id
	@Column(name="id_content_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}


	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "id_content_interaction_type")
	public ContentInteractionType getType() {
		return type;
	}

	public void setType(ContentInteractionType type) {
		this.type = type;
	}

	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

}
