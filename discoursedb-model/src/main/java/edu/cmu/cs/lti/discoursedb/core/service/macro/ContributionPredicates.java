package edu.cmu.cs.lti.discoursedb.core.service.macro;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.QContribution;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;

public final class ContributionPredicates {

	private ContributionPredicates() {
	}

	public static BooleanExpression contributionHasSourceId(String sourceId) {
		if (sourceId == null || sourceId.isEmpty()) {
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.dataSourceAggregate.sources.any().entitySourceId.eq(sourceId);
		}
	}
	public static BooleanExpression contributionHasDataSource(DataSourceInstance dataSource) {
		if (dataSource == null) {
			//TODO check if exists
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.dataSourceAggregate.sources.contains(dataSource);
		}
	}

}
