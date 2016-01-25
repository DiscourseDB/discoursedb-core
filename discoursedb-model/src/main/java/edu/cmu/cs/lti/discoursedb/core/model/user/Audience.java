package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionAudience;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="audience")
public class Audience extends TimedAnnotatableBaseEntityWithSource implements Serializable {

	private static final long serialVersionUID = -8464122652580037597L;

	@Id
	@Column(name="id_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "fk_audience_type")
	private AudienceType type;
	
    @OneToMany(mappedBy = "audience")
	private Set<ContributionAudience> audienceContributions = new HashSet<ContributionAudience>();
	
    @OneToMany(mappedBy = "audience")
	private Set<AudienceUser> audienceUsers = new HashSet<AudienceUser>();

    @OneToMany(mappedBy = "audience")
	private Set<AudienceGroup> audienceGroups = new HashSet<AudienceGroup>();
	
}
