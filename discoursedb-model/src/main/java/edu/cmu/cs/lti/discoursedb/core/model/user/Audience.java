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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionAudience;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"audienceContributions","audienceUsers","audienceGroups"})
@ToString(callSuper=true, exclude={"audienceContributions","audienceUsers","audienceGroups"})
@Entity
@Table(name="audience")
public class Audience extends TypedTimedAnnotatableSourcedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_audience", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
    @OneToMany(mappedBy = "audience")
	@Setter(AccessLevel.PRIVATE) 
	private Set<ContributionAudience> audienceContributions = new HashSet<ContributionAudience>();
	
    @OneToMany(mappedBy = "audience")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AudienceUser> audienceUsers = new HashSet<AudienceUser>();

    @OneToMany(mappedBy = "audience")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AudienceGroup> audienceGroups = new HashSet<AudienceGroup>();
	
}
