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
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

public final class DiscoursePartPredicates {

	private DiscoursePartPredicates() {
	}

	public static BooleanExpression discoursePartHasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.discourseToDiscourseParts.any().discourse.eq(discourse);
		}
	}

	public static BooleanExpression discoursePartHasName(String name) {
		if (name == null || name.isEmpty()) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.name.eq(name);
		}
	}

	public static BooleanExpression discoursePartHasType(DiscoursePartTypes type) {
		if (type == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.type.eq(type.name());
		}
	}
	
	public static BooleanExpression discoursePartHasDataSource(DataSourceInstance dataSource) {
		if (dataSource == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.dataSourceAggregate.sources.contains(dataSource);
		}
	}

}
