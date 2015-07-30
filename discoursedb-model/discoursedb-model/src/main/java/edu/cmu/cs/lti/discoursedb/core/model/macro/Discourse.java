package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name="discourse")
public class Discourse implements Serializable{

	private static final long serialVersionUID = -3736157436274230022L;

	private long id;
	
	private String name;
	
	private String source;
	
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();
	
	public Discourse(){}

	private Date version;
	@Version
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}
	
	@Id
	@Column(name="id_discourse", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

    @OneToMany(mappedBy = "discourse")
	public Set<DiscourseToDiscoursePart> getDiscourseToDiscourseParts() {
		return discourseToDiscourseParts;
	}

    public void setDiscourseToDiscourseParts(Set<DiscourseToDiscoursePart> discourseToDiscourseParts) {
		this.discourseToDiscourseParts = discourseToDiscourseParts;
	}
	
	
}
