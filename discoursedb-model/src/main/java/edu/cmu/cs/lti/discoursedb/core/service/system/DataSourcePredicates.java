package edu.cmu.cs.lti.discoursedb.core.service.system;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import edu.cmu.cs.lti.discoursedb.core.model.system.QDataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class DataSourcePredicates {

	private DataSourcePredicates() {}

	public static BooleanExpression hasSourceId(String entitySourceId) {
		return QDataSourceInstance.dataSourceInstance.entitySourceId.eq(entitySourceId);
	}
	public static BooleanExpression hasSourceType(DataSourceTypes type) {
		return QDataSourceInstance.dataSourceInstance.sourceType.eq(type);
	}
	
	public static BooleanExpression hasAssignedEntity(DataSources entitySourceAggregate) {
		return QDataSourceInstance.dataSourceInstance.sourceAggregate.id.eq(entitySourceAggregate.getId());
	}
	
	public static BooleanExpression hasDataSetName(String dataSetName) {
		return QDataSourceInstance.dataSourceInstance.datasetName.eq(dataSetName);
	}
	
	public static BooleanExpression hasEntitySourceDescriptor(String entitySourceDescriptor) {
		return QDataSourceInstance.dataSourceInstance.entitySourceDescriptor.eq(entitySourceDescriptor);
	}
}
