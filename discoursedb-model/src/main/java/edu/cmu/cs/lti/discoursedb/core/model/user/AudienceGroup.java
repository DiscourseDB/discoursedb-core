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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="audience_has_group", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_audience", "fk_group" }))
public class AudienceGroup extends TimedAnnotatableBaseEntity implements Serializable{
	
	private static final long serialVersionUID = 5232683398940704768L;

	private long id;
	
    private Group group;
    
    private Audience audience;
	
	public AudienceGroup() {}

	@Id
	@Column(name="id_audience_group", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_audience")
	public Audience getAudience() {
		return audience;
	}

	public void setAudience(Audience audience) {
		this.audience = audience;
	}

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_group")
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
    
	
}
