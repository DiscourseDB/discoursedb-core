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
package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * Wraps entities from the user table in prosolo. 
 * Comments are based on discussions with the prosolo developers.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloUser {
	private long id;
	private Date created;
	private String deleted;
	private String dc_description;
	private String title;
	private String avatar_url;
	private String lastname;
	private Double latitude;	
	private String location_name;
	private Double longitude;
	private String name;
	private String password;
	private Integer password_length;
	private String position;
	private String profile_url;
	private String sytem;
	private String user_type;
	private String email;
	private String user_user_organization;
}
