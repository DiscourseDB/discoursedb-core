package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * Aggregate entity that aggregates all DataSources of the associated entity.
 * It serves as a proxy for the entity for the purpose of source management.
 * A source can only have one entity, but an entity can have multiple sources.
 */
@Data
@EqualsAndHashCode(callSuper=true, exclude={"sources"})
@ToString(callSuper=true, exclude={"sources"})
@Entity
@Table(name="data_source_aggregate")
public class DataSourceAggregate extends BaseEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_data_sources", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@OneToMany(fetch=FetchType.LAZY, mappedBy="sourceAggregate")
    private Set<DataSourceInstance> sources = new HashSet<DataSourceInstance>();
	
	public void addSource(DataSourceInstance source) {
		this.sources.add(source);
	}
	
}
