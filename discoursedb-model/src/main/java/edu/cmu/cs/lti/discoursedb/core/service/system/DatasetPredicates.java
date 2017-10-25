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
package edu.cmu.cs.lti.discoursedb.core.service.system;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.system.QDataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.QDataset;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class DatasetPredicates {
	//TODO: SECURE
	private DatasetPredicates() {}
	
	public static BooleanExpression hasName(String name) {
		
		if(name==null ||name.isEmpty()) {
			return QDataset.dataset.isNull();
		}
		return QDataset.dataset.datasetName.eq(name);
	}
	
	public static BooleanExpression hasId(long id) {	
		return QDataset.dataset.datasetId.eq(id);
	}
	
	public static BooleanExpression isBlank() {	
		return QDataset.dataset.datasetName.eq("").or(
				QDataset.dataset.datasetName.isNull());
	}
}
