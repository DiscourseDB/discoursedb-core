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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="contribution_partof_discourse_part", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_contribution", "fk_discourse_part" }))
public class DiscoursePartContribution extends TimedAnnotatableBaseEntity implements Serializable{
	
	private static final long serialVersionUID = -7712508816782412933L;

	private long id;
	
    private Contribution contribution;
    
    private DiscoursePart discoursePart;
	
	public DiscoursePartContribution() {}

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution")
	public Contribution getContribution() {
		return contribution;
	}

	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}
    
	
}
