package edu.cmu.cs.lti.discoursedb.core.service.user;

import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.user.QUser;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class UserPredicates {

	private UserPredicates() {
	}

	public static BooleanExpression userHasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}

	public static BooleanExpression userHasUserName(String name) {
		if (name == null || name.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.username.eq(name);
		}
	}

	public static BooleanExpression userHasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QUser.user.isNull();
		} else {
			return QUser.user.discourses.contains(discourse);
		}
	}
	
	public static BooleanExpression userDataSourceType(DataSourceTypes type) {
		if (type == null) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().sourceType.eq(type);
		}
	}

	public static Predicate userHasDataSet(String dataSetName) {
		if (dataSetName == null || dataSetName.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().datasetName.eq(dataSetName);
		}
	}	
}
