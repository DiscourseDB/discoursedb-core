package edu.cmu.cs.lti.discoursedb.core.service.user;

import com.mysema.query.types.Predicate;

import edu.cmu.cs.lti.discoursedb.core.model.user.QUser;

public final class UserPredicates {

	private UserPredicates() {
	}

	/**
	 * Checks whether a user matches the provided sourceid in any of the datasources associated with them.
	 * 
	 * @param sourceId
	 * @return
	 */
	public static Predicate userHasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QUser.user.isNotNull();
		} else {
			return QUser.user.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}
}
