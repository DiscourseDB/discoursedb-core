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
package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationEntityProxy;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

@RepositoryRestResource(collectionResourceRel = "annotations", path = "annotations", exported=false)
public interface AnnotationEntityProxyRepository extends BaseRepository<AnnotationEntityProxy,Long>{
    
    //@Query("Select a FROM AnnotationInstance a left join a.annotatorEmail su where a.annotationEntityProxy=:aip and (su is null or su='') or su=?#{principal.username})")
    //HashSet<AnnotationInstance> findAllMyAnnotations(@Param("aip") AnnotationEntityProxy aip);

    @Query("Select a FROM AnnotationInstance a where a.annotationEntityProxy=:aip and (a.annotatorEmail  is null or a.annotatorEmail ='' or a.annotatorEmail =?#{principal.username})")
    HashSet<AnnotationInstance> findAllMyAnnotations(@Param("aip") AnnotationEntityProxy aip);

}
