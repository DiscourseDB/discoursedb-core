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
package edu.cmu.cs.lti.discoursedb.core.model.system;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Entity
@Table(name = "dataset", uniqueConstraints = {@UniqueConstraint(columnNames = { "dataset_id"}),
		@UniqueConstraint(columnNames= {"dataset_name" }) }, indexes = {
				@Index(name = "DatasetIndex", columnList = "dataset_id") })
public class Dataset extends BaseEntity implements Identifiable<Long> {

	/*
	 *   This table, like all tables in DiscourseDB, inherits dataset_id from BaseEntity. In Dataset, 
	 *   though, unlike other tables, it should be the primary key.  I'm not sure how to accomplish
	 *   that, so the workaround is that we have two columns: dataset_id is the inherited standard
	 *   place to keep the dataset identifier, and id_dataset is the primary key for this table.
	 *   In this table the two should always be equal.
	 */
	@Id
	@Column(name="id_dataset", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id; 
	
	@Column(name="dataset_name", nullable=false, updatable=false, length=95)
	private String datasetName;
	
	
	/**
	 * Creates a new DataSourceInstance for the entity with the source id
	 * "entitySourceId" based on the dataset with the provided name
	 * "datasetName" which is an instance of the source with the provided name
	 * "sourceType".
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 */
	public Dataset(String datasetName) {
		setDatasetName(datasetName);
		
	}
	

}
