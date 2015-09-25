package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="discourse_part_relation")
public class DiscoursePartRelation extends TimedAnnotatableBaseEntity implements Serializable {

	private static final long serialVersionUID = 1914547709687781470L;

	private long id;
	
	private DiscoursePart source;
	
	private DiscoursePart target;
	
	private DiscoursePartRelationType type;
	
	public DiscoursePartRelation(){}
	
	@Id
	@Column(name="id_discourse_part_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	public DiscoursePart getSource() {
		return source;
	}

	public void setSource(DiscoursePart source) {
		this.source = source;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	public DiscoursePart getTarget() {
		return target;
	}

	public void setTarget(DiscoursePart target) {
		this.target = target;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_part_relation_type")
	public DiscoursePartRelationType getType() {
		return type;
	}

	public void setType(DiscoursePartRelationType type) {
		this.type = type;
	}
	
}
