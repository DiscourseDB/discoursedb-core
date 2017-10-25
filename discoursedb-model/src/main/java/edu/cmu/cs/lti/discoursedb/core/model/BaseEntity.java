/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import edu.cmu.cs.lti.discoursedb.core.model.system.Dataset;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DatasetRepository;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
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
	
	@Column(name = "dataset_id", nullable=false)
	@Description("Dataset id controls who can access this element")
	private Long datasetId;	
	
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
