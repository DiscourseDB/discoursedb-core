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
@Table(name="contribution_type")
public class ContributionType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = -4187467243916373251L;

	private long id;
		
	private Set<Contribution> contributions = new HashSet<Contribution>();

	public ContributionType(){}
		
	@Id
	@Column(name="id_contribution_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<Contribution> getContributions() {
		return contributions;
	}

	public void setContributions(Set<Contribution> contributions) {
		this.contributions = contributions;
	}
}
