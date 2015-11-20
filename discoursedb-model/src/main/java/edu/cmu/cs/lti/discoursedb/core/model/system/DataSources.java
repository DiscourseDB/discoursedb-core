package edu.cmu.cs.lti.discoursedb.core.model.system;

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

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;

/**
 * Aggregate entity that aggregates all DataSources of the associated entity.
 * It serves as a proxy for the entity for the purpose of source management.
 * A source can only have one entity, but an entity can have multiple sources.
 */
@Entity
@Table(name="data_sources")
public class DataSources extends UntimedBaseEntity implements Serializable{

	private static final long serialVersionUID = -6582983183583393074L;

	private long id;
	
    private Set<DataSourceInstance> sources = new HashSet<DataSourceInstance>();
	
	public DataSources(){}

	@Id
	@Column(name="id_data_sources", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL, mappedBy="sourceAggregate")
	public Set<DataSourceInstance> getSources() {
		return sources;
	}

	public void setSources(Set<DataSourceInstance> sources) {
		this.sources = sources;
	}

	public void addSource(DataSourceInstance source) {
		this.sources.add(source);
	}
	
}
