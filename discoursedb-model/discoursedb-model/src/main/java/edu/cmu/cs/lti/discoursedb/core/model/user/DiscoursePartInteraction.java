package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.Date;

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

import edu.cmu.cs.lti.discoursedb.core.model.CoreBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part_interaction")
public class DiscoursePartInteraction extends CoreBaseEntity implements Serializable{

	private static final long serialVersionUID = -7782010595781927999L;

	private long id;
	
	private User user;
	
	private DiscoursePart discoursepart;
	
	private Date startTime;
	
	private Date endTime;
	
	private DiscoursePartInteractionType type;
	
	private Annotations annotations;
	
	public DiscoursePartInteraction(){}
	
	@Id
	@Column(name="id_content_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	 @ManyToOne(cascade = CascadeType.ALL)
	 @JoinColumn(name = "fk_user")
	 public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part")
	public DiscoursePart getDiscoursePart() {
		return discoursepart;
	}

	public void setDiscoursePart(DiscoursePart discoursepart) {
		this.discoursepart = discoursepart;
	}

	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part_interaction_type")
	public DiscoursePartInteractionType getType() {
		return type;
	}

	public void setType(DiscoursePartInteractionType type) {
		this.type = type;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

}
