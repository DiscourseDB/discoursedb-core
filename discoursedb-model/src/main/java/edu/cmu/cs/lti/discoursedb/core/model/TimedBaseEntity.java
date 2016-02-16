package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Adds basic fields to entities that do not keep track of their lifetime (version, entity creation date)
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class TimedBaseEntity extends BaseEntity{
	
	@Column(name = "start_time")
	@Temporal(TemporalType.TIMESTAMP)
	@Description("Start of the lifetime of this entity, e.g. creation date of a contribution.")
    private Date startTime;   

	@Column(name = "end_time")
	@Temporal(TemporalType.TIMESTAMP)
	@Description("End of the lifetime of this entity, e.g. deletion date of a contribution.")
    private Date endTime;

}
