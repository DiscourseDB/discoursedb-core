package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Adds basic fields to entities that do not keep track of their lifetime (version, entity creation date)
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class TimedBaseEntity{

	@JsonIgnore
	@Version
	@Setter(AccessLevel.PRIVATE) 
	@Description("The version of this entity. Only used for auditing purposes and changes whenever the entity is modified.")
	private Long version;	
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The date this entity was first stored in the database. Only used for auditing purposes.")
	private Date createDate;
	
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	@Description("Start of the lifetime of this entity, e.g. creation date of a contribution.")
    private Date startTime;   

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	@Description("End of the lifetime of this entity, e.g. deletion date of a contribution.")
    private Date endTime;

}
