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
 * Wraps entities form the post table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloPost {
 private String dtype;
 private Long id;
 private Date created;
 private Boolean deleted;
 private String dc_description;
 private String title;
 private String content;
 private String link;
 private String visibility;
 private Boolean connect_with_status;
 private Long maker;
 private Long reshare_of;
 private Long rich_content;
 private Long goal;
 private String post_link;

}
