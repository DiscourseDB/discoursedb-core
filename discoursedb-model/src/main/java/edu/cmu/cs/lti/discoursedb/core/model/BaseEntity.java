package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Common subtype of all DiscourseDB entities
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class BaseEntity{
	
	@JsonIgnore
	@Version
	@Column(name = "entity_version")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The version of this entity. Only used for auditing purposes and changes whenever the entity is modified.")
	private Long entityVersion;	
	
	@JsonIgnore
	@CreationTimestamp
	@Column(name = "entity_created")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The date this entity was first stored in the database. Only used for auditing purposes.")
	private Date entityCreationTime;

	@JsonIgnore
	@LastModifiedDate
	@Column(name = "entity_modified")
	@Setter(AccessLevel.PRIVATE) 
	@Description("The date this entity was last modified. Only used for auditing purposes.")
	private Date entityModificationTime;

	@PrePersist
    public void prePersist(){
		Date now = new Date();
        this.entityCreationTime = now;
        this.entityModificationTime = now;
    }

	@PreUpdate
    public void preUpdate() {
        this.entityModificationTime = new Date();
    }
}
