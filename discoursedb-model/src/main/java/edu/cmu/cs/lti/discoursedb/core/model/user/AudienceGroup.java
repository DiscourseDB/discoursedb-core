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
import javax.persistence.UniqueConstraint;

import edu.cmu.cs.lti.discoursedb.core.model.TimedBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="audience_has_group", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_audience", "fk_group" }))
public class AudienceGroup extends TimedBaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5232683398940704768L;

	@Id
	@Column(name="id_audience_group", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_group")
    private Group group;
    
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_audience")
    private Audience audience;
	
}
