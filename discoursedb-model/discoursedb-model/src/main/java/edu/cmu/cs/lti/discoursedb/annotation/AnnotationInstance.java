package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.system.DiscourseDB;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="annotation_instance")
public class AnnotationInstance implements Serializable{

	private static final long serialVersionUID = 6699029374236146557L;
    
    private long id;
	
	private int beginOffset;
	
	private int endOffset;
	
	private String coveredText;
	
	private Timestamp startTime;
	
	private Timestamp endTime;	
	
	private AnnotationType type;	
	
	private DiscourseDB discoursedb;
	
	private Set<Feature> features = new HashSet<Feature>();
	
	public AnnotationInstance(){}

	@Id
	@Column(name="id_annotation_intance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBeginOffset() {
		return beginOffset;
	}

	public void setBeginOffset(int beginOffset) {
		this.beginOffset = beginOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
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

	public AnnotationType getType() {
		return type;
	}

	public void setType(AnnotationType type) {
		this.type = type;
	}

	public DiscourseDB getDiscoursedb() {
		return discoursedb;
	}

	public void setDiscoursedb(DiscourseDB discoursedb) {
		this.discoursedb = discoursedb;
	}

	@OneToMany
	@JoinColumn(name="id_feature")
	public Set<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}
	
	
	
}
