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
package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;

public class RecommendationDataSourceInstanceResource extends ResourceSupport {
	
	private String entitySourceId;
	private String entitySourceDecriptor;
	private String sourceType;	
	private String datasetName;	
	
	public RecommendationDataSourceInstanceResource(DataSourceInstance dataSource) {
		setEntitySourceId(dataSource.getEntitySourceId());
		setEntitySourceDecriptor(dataSource.getEntitySourceDescriptor());
		setSourceType(dataSource.getSourceType().name());
		setDatasetName(dataSource.getDatasetName());
	}
	
	public String getEntitySourceId() {
		return entitySourceId;
	}


	public void setEntitySourceId(String entitySourceId) {
		this.entitySourceId = entitySourceId;
	}


	public String getSourceType() {
		return sourceType;
	}


	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}


	public String getDatasetName() {
		return datasetName;
	}


	public void setDatasetName(String datasetName) {
		this.datasetName = nullCheck(datasetName);
	}


	private String nullCheck(String s){
		return s==null?"":s;
	}

	public String getEntitySourceDecriptor() {
		return entitySourceDecriptor;
	}

	public void setEntitySourceDecriptor(String entitySourceDecriptor) {
		this.entitySourceDecriptor = entitySourceDecriptor;
	}

}
