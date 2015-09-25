package edu.cmu.cs.lti.discoursedb.core.service.user;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.user.QUser;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class UserPredicates {

	private UserPredicates() {
	}

	public static Predicate userHasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QUser.user.isNotNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}
	
	public static Predicate userHasSourceIdAndDataSourceType(String sourceId, DataSourceTypes type) {
		if (sourceId == null || sourceId.isEmpty() || type == null) {
			return QUser.user.isNull();
		} else {
			BooleanExpression hasEntitySourceId = QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
			BooleanExpression hasSourceType = QUser.user.dataSourceAggregate.sources.any().sourceType.eq(type);
			return hasEntitySourceId.and(hasSourceType);					
		}
	}

	public static Predicate userHasSourceIdAndDataSetName(String sourceId, String dataSetName) {
		if (sourceId == null || sourceId.isEmpty() || dataSetName == null || dataSetName.isEmpty()) {
			return QUser.user.isNull();
		} else {
			BooleanExpression hasEntitySourceId = QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
			BooleanExpression hasDataSetName = QUser.user.dataSourceAggregate.sources.any().datasetName.eq(dataSetName);
			return hasEntitySourceId.and(hasDataSetName);					
		}
	}
	
	public static Predicate userHasSourceIdAndDataSourceTypeAndDataSetName(String sourceId, DataSourceTypes type, String dataSetName) {
		if (sourceId == null || sourceId.isEmpty() || dataSetName == null || dataSetName.isEmpty()|| type == null) {
			return QUser.user.isNull();
		} else {
			BooleanExpression hasSourceType = QUser.user.dataSourceAggregate.sources.any().sourceType.eq(type);
			BooleanExpression hasEntitySourceId = QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
			BooleanExpression hasDataSetName = QUser.user.dataSourceAggregate.sources.any().datasetName.eq(dataSetName);
			return hasEntitySourceId.and(hasDataSetName.and(hasSourceType));					
		}
	}
}
