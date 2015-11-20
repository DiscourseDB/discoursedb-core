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

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@Table(name="discourse_relation")
public class DiscourseRelation extends TimedAnnotatableBaseEntity implements Serializable {

	private static final long serialVersionUID = -2533440012916999219L;

	private long id;
	
	private Contribution source;
	
	private Contribution target;
	
	private DiscourseRelationType type;
	
	public DiscourseRelation(){}
	
	@Id
	@Column(name="id_discourse_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	public Contribution getSource() {
		return source;
	}

	public void setSource(Contribution source) {
		this.source = source;
	}

	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	public Contribution getTarget() {
		return target;
	}

	public void setTarget(Contribution target) {
		this.target = target;
	}

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_discourse_relation_type")
	public DiscourseRelationType getType() {
		return type;
	}

	public void setType(DiscourseRelationType type) {
		this.type = type;
	}

}
