package edu.cmu.cs.lti.discoursedb.core.service.system;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.system.QDataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class DataSourcePredicates {

	private DataSourcePredicates() {
	}

	public static BooleanExpression dataSourceInstanceHasSourceId(String entitySourceId) {
		if (entitySourceId == null||entitySourceId.isEmpty()) {
			return QDataSourceInstance.dataSourceInstance.isNull();
		} else {
			
			return QDataSourceInstance.dataSourceInstance.entitySourceId.eq(entitySourceId);
		}
	}
	public static BooleanExpression dataSourceInstanceHasSourceType(DataSourceTypes type) {
		if (type == null) {
			return QDataSourceInstance.dataSourceInstance.isNull();
		} else {
			
			return QDataSourceInstance.dataSourceInstance.sourceType.eq(type);
		}
	}
	
	public static BooleanExpression dataSourceInstanceHasDataSetName(String dataSetName) {
		if (dataSetName == null||dataSetName.isEmpty()) {
			return QDataSourceInstance.dataSourceInstance.isNull();
		} else {
			
			return QDataSourceInstance.dataSourceInstance.datasetName.eq(dataSetName);
		}
	}

}
