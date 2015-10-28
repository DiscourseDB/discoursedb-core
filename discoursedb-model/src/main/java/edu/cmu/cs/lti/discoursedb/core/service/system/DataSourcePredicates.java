package edu.cmu.cs.lti.discoursedb.core.service.system;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import edu.cmu.cs.lti.discoursedb.core.model.system.QDataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class DataSourcePredicates {

	private DataSourcePredicates() {}

	public static BooleanExpression hasSourceId(String entitySourceId) {
		if(entitySourceId==null||entitySourceId.isEmpty()){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.entitySourceId.eq(entitySourceId);
	}
	public static BooleanExpression hasSourceType(DataSourceTypes type) {
		if(type==null){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.sourceType.eq(type);
	}
	
	public static BooleanExpression hasAssignedEntity(DataSources entitySourceAggregate) {
		if(entitySourceAggregate==null){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.sourceAggregate.id.eq(entitySourceAggregate.getId());
	}
	
	public static BooleanExpression hasDataSetName(String dataSetName) {
		if(dataSetName==null||dataSetName.isEmpty()){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.datasetName.eq(dataSetName);
	}
	
	public static BooleanExpression hasEntitySourceDescriptor(String entitySourceDescriptor) {
		if(entitySourceDescriptor==null||entitySourceDescriptor.isEmpty()){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.entitySourceDescriptor.eq(entitySourceDescriptor);
	}
}
