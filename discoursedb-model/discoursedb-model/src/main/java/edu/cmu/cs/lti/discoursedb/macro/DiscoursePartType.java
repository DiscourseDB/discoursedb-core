package edu.cmu.cs.lti.discoursedb.macro;

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

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part_type")
public class DiscoursePartType implements Serializable {

	private static final long serialVersionUID = 532051260123187170L;

	private long id;
	
	private String type;
	
    private Set<DiscoursePart> discourseParts=new HashSet<DiscoursePart>();
	
	public DiscoursePartType(){}

	@Id
	@Column(name="id_discourse_part_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<DiscoursePart> getDiscourseParts() {
		return discourseParts;
	}

	public void setDiscourseParts(Set<DiscoursePart> discourseParts) {
		this.discourseParts = discourseParts;
	}
}
