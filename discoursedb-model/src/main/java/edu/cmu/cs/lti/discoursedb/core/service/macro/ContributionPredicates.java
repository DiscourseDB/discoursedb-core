/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.core.service.macro;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QContribution;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;

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
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.dataSourceAggregate.sources.contains(dataSource);
		}
	}
	
	public static BooleanExpression contributionHasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.contributionPartOfDiscourseParts.any().discoursePart.discourseToDiscourseParts.any().discourse.eq(discourse);
		}
	}
	
	public static BooleanExpression contributionHasType(ContributionTypes type) {
		if (type == null) {
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.type.eq(type.name());
		}
	}

	public static BooleanExpression contributionHasDiscoursePart(DiscoursePart discoursePart) {
		if (discoursePart == null) {
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.contributionPartOfDiscourseParts.any().discoursePart.eq(discoursePart);
		}
	}
	
	/**
	 * This only returns true if the first revision was created by the provided user - not any other revisions
	 * 
	 * @param user the user to look for
	 * @return true, if the first revision of the contribution was created by the provided user
	 */
	public static BooleanExpression contributionWithFirstRevisionByUser(User user) {
		if (user == null) {
			return QContribution.contribution.isNull();
		} else {
			return QContribution.contribution.firstRevision.author.eq(user);
		}
	}

}
