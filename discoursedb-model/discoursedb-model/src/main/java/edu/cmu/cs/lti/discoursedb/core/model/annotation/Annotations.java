package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="annotation")
public class Annotations implements Serializable{

	private static final long serialVersionUID = -4654984158138436217L;

	private long id;
	
    private Set<AnnotationInstance> annotations = new HashSet<AnnotationInstance>();
    
	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}

	public Annotations(){}
	
	@Id
	@Column(name="id_annotation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="annotationAggregate")
	public Set<AnnotationInstance> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<AnnotationInstance> annotations) {
		this.annotations = annotations;
	}

}
