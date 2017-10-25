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
package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public interface ContentRepository extends BaseRepository<Content,Long>{
	public List<Content> findByIdIn(List<Long> contentIdList);
	
	@Modifying
	@Query(value="update content c set c.fk_next_revision = ?2 where c.id_content = ?1",nativeQuery=true)
	public void setNextRevisionId(Long id, Long nextRevId);

	@Modifying
	@Query(value="update content c set c.fk_previous_revision = ?2 where c.id_content = ?1",nativeQuery=true)
	public void setPreviousRevisionId(Long id, Long previousRevId);
	
	@Query("select c from Content c left join c.dataSourceAggregate dsa left join dsa.sources dsi where dsi.entitySourceId=:id"
			+ " and dsi.entitySourceDescriptor=:entitySourceDescriptor and dsi.sourceType =:sourceType and dsi.datasetId=:datasetId")
	Optional<DiscoursePart> findOneByDataSource(@Param("id") String id, @Param("entitySourceDescriptor") String entitySourceDescriptor, @Param("sourceType") DataSourceTypes sourceType,
			@Param("datasetId") Long datasetId);
   
}
