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
package edu.cmu.cs.lti.discoursedb.core.model.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableBE;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "contribution_interaction", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "fk_user", "type", "fk_contribution" }),
		@UniqueConstraint(columnNames = { "fk_user", "type", "fk_content" }) })
public class ContributionInteraction extends TypedTimedAnnotatableBE implements Identifiable<Long> {

	@Id
	@Column(name="id_contribution_interaction", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="userPerformedInteraction",path="userPerformedInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_user")
	private User user;
	
	@RestResource(rel="contributionOfInteraction",path="contributionOfInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_contribution")
	private Contribution contribution;

	@RestResource(rel="contentOfInteraction",path="contentOfInteraction")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "fk_content")
	private Content content;
	
}
