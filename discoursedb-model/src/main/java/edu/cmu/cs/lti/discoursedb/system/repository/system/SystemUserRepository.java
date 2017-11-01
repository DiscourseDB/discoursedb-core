/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author: Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.system.repository.system;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserProperty;

@Transactional
public interface SystemUserRepository extends BaseRepository<SystemUser,Long>{

	//@Query("select u from SystemUser su where su.email=:email")
	Optional<SystemUser> findOneByEmail(@Param("email") String email);
	
    //@Query("select u from SystemUser su where su.username=:username")
	Optional<SystemUser> findOneByUsername(@Param("username") String username);
	
	@Query("select sup from SystemUserProperty  sup where sup.systemUser=:username and sup.propType like :prop_type")
	List<SystemUserProperty> findProperties(@Param("username") SystemUser username, @Param("prop_type") String prop_type);

	@Query("select sup from SystemUserProperty sup where sup.systemUser=:username and sup.propName=:prop_name and sup.propType=:prop_type")
	Optional<SystemUserProperty> getProperty(@Param("username") SystemUser username, 
			@Param("prop_type") String prop_type, 
			@Param("prop_name") String prop_name);

	@Modifying
	@Query("delete from SystemUserProperty sup  where sup.systemUser=:username and sup.propName=:prop_name and sup.propType=:prop_type")
	int deleteProperty(@Param("username") SystemUser username, 
			@Param("prop_type") String prop_type, 
			@Param("prop_name") String prop_name);
	
	@Modifying
	@Query(value="insert into system_user_property (prop_type, prop_name, prop_value, fk_system_user) VALUES "
			+ "(:prop_type, :prop_name, :prop_value, :system_user_id)", nativeQuery=true)
	int createProperty(@Param("prop_type") String prop_type, @Param("prop_name") String prop_name, 
			@Param("prop_value") String prop_value, @Param("system_user_id") long system_user_id );
}
