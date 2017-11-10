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
package edu.cmu.cs.lti.discoursedb.core.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;


/**
 * The CoreBaseRepository interface defines the data access methods that every of the DiscourseDB Core repositories should have.
 * It extends DiscourseDB-forks of the Spring-Data repositories PagingAndSortingRepository, CrudRepository and QueryDslPredicateExecutor.
 * 
 * @author Oliver Ferschke
 *
 * @param <T> the entity type
 * @param <ID> the primary key type (usually long)
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends CustomPagingAndSortingRepository<T, ID>, CustomQueryDslPredicateExecutor<T> {
	
	
}
