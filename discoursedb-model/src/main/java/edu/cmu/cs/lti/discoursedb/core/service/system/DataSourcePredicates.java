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
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public final class DataSourcePredicates {

	private DataSourcePredicates() {}

	public static BooleanExpression hasSourceId(String entitySourceId) {
		if(entitySourceId==null||entitySourceId.isEmpty()){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.entitySourceId.eq(entitySourceId);
	}
	public static BooleanExpression hasDatasetId(long id) {
		return QDataSourceInstance.dataSourceInstance.datasetId.eq(id);
	}
	public static BooleanExpression hasSourceType(DataSourceTypes type) {
		if(type==null){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.sourceType.eq(type);
	}
	
	public static BooleanExpression hasAssignedEntity(DataSourceAggregate entitySourceAggregate) {
		if(entitySourceAggregate==null){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.sourceAggregate.id.eq(entitySourceAggregate.getId());
	}
	
	public static BooleanExpression hasEntitySourceDescriptor(String entitySourceDescriptor) {
		if(entitySourceDescriptor==null||entitySourceDescriptor.isEmpty()){
			return QDataSourceInstance.dataSourceInstance.isNull();
		}
		return QDataSourceInstance.dataSourceInstance.entitySourceDescriptor.eq(entitySourceDescriptor);
	}
}
