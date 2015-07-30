package edu.cmu.cs.lti.discoursedb.core.model.macro;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_relation")
public class DiscourseRelation implements Serializable {

	private static final long serialVersionUID = -2533440012916999219L;

	private long id;
	
	private Contribution source;
	
	private Contribution target;
	
	private Date startTime;
	
	private Date endTime;
	
	private DiscourseRelationType type;
	
	private Annotations annotations;
	
	public DiscourseRelation(){}

	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}
	
	@Id
	@Column(name="id_discourse_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source",insertable=false,updatable=false, nullable=false)
	public Contribution getSource() {
		return source;
	}

	public void setSource(Contribution source) {
		this.source = source;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source",insertable=false,updatable=false, nullable=false)
	public Contribution getTarget() {
		return target;
	}

	public void setTarget(Contribution target) {
		this.target = target;
	}

	@Column(name = "start_time")
	@Temporal(TemporalType.DATE)
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@Column(name = "end_time")
	@Temporal(TemporalType.DATE)
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_relation_type")
	public DiscourseRelationType getType() {
		return type;
	}

	public void setType(DiscourseRelationType type) {
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
