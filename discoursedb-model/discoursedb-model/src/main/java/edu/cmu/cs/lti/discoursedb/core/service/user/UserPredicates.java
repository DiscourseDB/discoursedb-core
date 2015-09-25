package edu.cmu.cs.lti.discoursedb.core.service.user;

import com.mysema.query.types.Predicate;

import edu.cmu.cs.lti.discoursedb.core.model.user.QUser;

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

//The following predicates need to check whether the properties occur in a single DataSourceInstance 
	
//	public static Predicate userHasSourceIdAndDataSourceType(String sourceId, DataSourceTypes type) {
//		if (sourceId == null || sourceId.isEmpty() || type == null) {
//			return QUser.user.isNotNull();
//		} else {
//			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId)
//					.and(QUser.user.dataSourceAggregate.sources.any().sourceType.eq(type));
//		}
//	}
//
//	public static Predicate userHasSourceIdAndDataSetName(String sourceId, String dataSetName) {
//		if (sourceId == null || sourceId.isEmpty() || dataSetName == null || dataSetName.isEmpty()) {
//			return QUser.user.isNotNull();
//		} else {
//			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId)
//					.and(QUser.user.dataSourceAggregate.sources.any().datasetName.eq(dataSetName));
//		}
//	}
}
