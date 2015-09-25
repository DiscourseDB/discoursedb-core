package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;

public class RecommendationDataSourceInstanceResource extends ResourceSupport {
	
	private String entitySourceId;
	private String sourceType;	
	private String datasetName;	
	
	public RecommendationDataSourceInstanceResource(DataSourceInstance dataSource) {
		setEntitySourceId(dataSource.getEntitySourceId());
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

}
