package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Adds basic common fields for type entities (Version, CreationDate, Type identifier) 
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper=true)
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class TypedBE extends BaseEntity{
	
	@Column(unique=true)
	@Description("The type value that this type-entity represents.")
	private String type;

}
