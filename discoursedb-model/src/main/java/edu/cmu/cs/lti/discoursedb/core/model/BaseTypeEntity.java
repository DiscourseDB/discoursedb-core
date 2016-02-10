package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Adds basic common fields for type entities (Version, CreationDate, Type identifier) 
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class BaseTypeEntity{

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
	
	@Column(unique=true)
	@Description("The type value that this type-entity represents.")
	private String type;

}
