package edu.cmu.cs.lti.discoursedb.core.model.macro;

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

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;

@Entity
@Table(name="discourse_part_type")
public class DiscoursePartType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = 532051260123187170L;

	private long id;
	
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

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<DiscoursePart> getDiscourseParts() {
		return discourseParts;
	}

	public void setDiscourseParts(Set<DiscoursePart> discourseParts) {
		this.discourseParts = discourseParts;
	}
}
