package edu.cmu.cs.lti.discoursedb.core.service.user;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.user.QUser;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class UserPredicates {

	private UserPredicates() {
	}

	/**
	 * Checks whether a user is associated with the given source id in any of their data source
	 * 
	 * @param sourceId the source id to check in the list of DataSourceInstances associated with the user
	 * @return
	 */
	public static BooleanExpression hasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}

	/**
	 * Checks whether a user has the given username
	 * 
	 * @param name the username the user should have
	 * @return
	 */
	public static BooleanExpression hasUserName(String name) {
		if (name == null || name.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.username.eq(name);
		}
	}

	/**
	 * Checks whether a user is associated with the given discourse
	 * 
	 * @param discourse the discourse to check 
	 * @return
	 */
	public static BooleanExpression hasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QUser.user.isNull();
		} else {
			return QUser.user.discourses.contains(discourse);
		}
	}
	
	/**
	 * Checks whether a user is associated with a data source of the given type (e.g. EDX)
	 * 
	 * @param type the data source type
	 * @return
	 */
	public static BooleanExpression hasDataSourceType(DataSourceTypes type) {
		if (type == null) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().sourceType.eq(type);
		}
	}

	/**
	 * Checks whether a User is associated with a given dataset.
	 * 
	 * @param dataSetName name of the dataset
	 * @return 
	 */
	public static BooleanExpression hasDataSet(String dataSetName) {
		if (dataSetName == null || dataSetName.isEmpty()) {
			return QUser.user.isNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().datasetName.eq(dataSetName);
		}
	}	
}
