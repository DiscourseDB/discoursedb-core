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

import org.springframework.data.rest.core.annotation.Description;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.user.Audience;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="contribution_has_audience")
@Description("A relation entity that associates a contribution with an audience.")
public class ContributionAudience extends TimedBaseEntity implements Serializable{
	
	private static final long serialVersionUID = -2668707116929576568L;

	@Id
	@Column(name="id_contribution_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution")
    @Description("The contribution that should be related to an audience.")
    private Contribution contribution;
    
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_audience")
    @Description("The audience that should be related to a contribution.")
    private Audience audience;
    		
}
