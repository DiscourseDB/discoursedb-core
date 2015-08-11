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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="contribution_partof_discourse_part", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_contribution", "fk_discourse_part" }))
public class DiscoursePartContribution implements Serializable{
	
	private static final long serialVersionUID = -7712508816782412933L;

	private long id;
	
    private Contribution contribution;
    
    private DiscoursePart discoursePart;
    
    private Date startTime;
    
    private Date endTime;
	
    private Annotations annotations;
    
	public DiscoursePartContribution() {}
    
	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
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

	@Id
	@Column(name="id_discourse_part_contribution", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_discourse_part")
	public DiscoursePart getDiscoursePart() {
		return discoursePart;
	}

	public void setDiscoursePart(DiscoursePart discoursePart) {
		this.discoursePart = discoursePart;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation")
	public Annotations getAnnotations() {
		return annotations;
	}


	public void setAnnotations(Annotations annotations) {
		this.annotations = annotations;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution")
	public Contribution getContribution() {
		return contribution;
	}

	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}
    
	
}
