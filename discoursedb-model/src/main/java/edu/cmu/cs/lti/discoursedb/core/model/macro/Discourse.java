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
package edu.cmu.cs.lti.discoursedb.core.model.macro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>A Discourse represents the broad context of interactions that might come from
 * multiple datasets. For example, a Discourse could represent an installment of
 * an online course. All interactions in the context of this course -
 * independent from the source dataset - will be associated with this Discourse
 * instance. Another installment of the same course would be represented by a
 * new Discourse instance.</p> 
 * 
 * <p>A Discourse is associated to one or more
 * DiscoursePart instances which represent sub-spaces in the realm of the
 * Discourse. That is, an online course with a discussion forum and chat would
 * have two DiscoursePart instances associated with its Discourse instance which
 * represent these two discussion spaces.</p>
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true, exclude={"discourseToDiscourseParts","users"})
@ToString(callSuper=true, exclude={"discourseToDiscourseParts","users"})
@Entity
@Table(name = "discourse")
public class Discourse extends BaseEntity implements Identifiable<Long> {

	public Discourse(String name){
		Assert.hasText(name);
		this.name=name;
	}

	@Id
	@Column(name = "id_discourse", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;

	@Column(updatable=false, unique=true, columnDefinition="TEXT")
	private String name;

	@OneToMany(mappedBy = "discourse")
	@Setter(AccessLevel.PRIVATE) 
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "discourses")
	@Setter(AccessLevel.PRIVATE) 
	private Set<User> users;
	
}
