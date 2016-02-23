package edu.cmu.cs.lti.discoursedb.core.model.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionAudience;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"audienceContributions","audienceUsers","audienceGroups"})
@ToString(callSuper=true, exclude={"audienceContributions","audienceUsers","audienceGroups"})
@Entity
@Table(name="audience")
public class Audience extends TypedTimedAnnotatableSourcedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
    @OneToMany(mappedBy = "audience")
	private Set<ContributionAudience> audienceContributions = new HashSet<ContributionAudience>();
	
    @OneToMany(mappedBy = "audience")
	private Set<AudienceUser> audienceUsers = new HashSet<AudienceUser>();

    @OneToMany(mappedBy = "audience")
	private Set<AudienceGroup> audienceGroups = new HashSet<AudienceGroup>();
	
}
