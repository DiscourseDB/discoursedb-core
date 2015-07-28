package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
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

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="audience")
public class Audience implements Serializable {

	private static final long serialVersionUID = -8464122652580037597L;

	@Id
	@Column(name="id_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private AudienceType type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Audience(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AudienceType getType() {
		return type;
	}

	public void setType(AudienceType type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
