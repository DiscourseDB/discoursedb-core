package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="audience_type")
public class AudienceType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = 2596255302608073060L;

	private long id;
	
	private Set<Audience> audiences = new HashSet<Audience>();
	
	@Id
	@Column(name="id_audience_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AudienceType(){}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<Audience> getAudiences() {
		return audiences;
	}

	public void setAudiences(Set<Audience> audiences) {
		this.audiences = audiences;
	}
}
