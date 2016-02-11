package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.io.Serializable;
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

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"contributions"})
@ToString(callSuper=true, exclude={"contributions"})
@Entity
@Table(name="contribution_type")
public class ContributionType extends BaseTypeEntity implements Serializable {

	private static final long serialVersionUID = -4187467243916373251L;

	@Id
	@Column(name="id_contribution_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
		
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	private Set<Contribution> contributions = new HashSet<Contribution>();

}
