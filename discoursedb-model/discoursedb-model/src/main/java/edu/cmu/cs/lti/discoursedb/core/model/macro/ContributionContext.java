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
@Table(name="contribution_has_context", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_contribution", "fk_context" }))
public class ContributionContext extends TimedAnnotatableBaseEntity implements Serializable{
	
	private static final long serialVersionUID = -1542771414387707049L;

	private long id;
	
    private Contribution contribution;
    
    private Context context;
	
	public ContributionContext() {}

	@Id
	@Column(name="id_contribution_context", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution")
	public Contribution getContribution() {
		return contribution;
	}

	public void setContribution(Contribution contribution) {
		this.contribution = contribution;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_context")
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
    
	
}
