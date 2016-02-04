package edu.cmu.cs.lti.discoursedb.core.model.user;

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

import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="audience_has_user")
public class AudienceUser extends TimedBaseEntity implements Serializable{
	
	private static final long serialVersionUID = -6740377434969542427L;

	@Id
	@Column(name="id_audience_user", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="userMemberOfAudiences",path="userMemberOfAudiences")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
    private User user;
    
	@RestResource(rel="audienceHasUsers",path="audienceHasUsers")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_audience")
    private Audience audience;
    
}
