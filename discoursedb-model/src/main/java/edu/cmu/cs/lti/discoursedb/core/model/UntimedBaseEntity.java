package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;

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
public abstract class UntimedBaseEntity{

	@JsonIgnore
	@Version
	@Setter(AccessLevel.PRIVATE) 
	private Long version;	
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "created")
	@Setter(AccessLevel.PRIVATE) 
	private Date createDate;
}
