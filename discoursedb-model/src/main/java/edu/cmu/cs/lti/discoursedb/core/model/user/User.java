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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A User is uniquely identified by a source_id,username pair. Neither source_id
 * nor username alone are unique. However, source_id and username are unique in
 * the context of a given Discourse.
 * 
 * TODO: We should make sure that source_id and username each are unique within a certain Discourse context
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true, exclude={"contentInteractions","userAudiences","userGroups","sourceOfUserRelations","targetOfUserRelations"})
@ToString(callSuper=true, exclude={"contentInteractions","userAudiences","userGroups","sourceOfUserRelations","targetOfUserRelations"})
@Entity
@Table(name = "user", indexes = { @Index(name = "userNameIndex", columnList = "username") })
public class User extends TimedAnnotatableSourcedBE implements Identifiable<Long> {

	@Id
	@Column(name = "id_user", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;

	private String realname;

	@Column(length=191, nullable=false, unique=true)
	private String username;

	private String email;

	private String ip;

	private String language;

	private String country;

	private String location;

	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
	@JoinTable(name = "user_memberof_discourse", joinColumns = {
		@JoinColumn(name = "id_user", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "id_discourse", nullable = false, updatable = false) })
	private Set<Discourse> discourses = new HashSet<Discourse>();

	@OneToMany(mappedBy = "user")
	@Setter(AccessLevel.PRIVATE) 
	private Set<ContributionInteraction> contentInteractions = new HashSet<ContributionInteraction>();

	@OneToMany(mappedBy = "user")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AudienceUser> userAudiences = new HashSet<AudienceUser>();
	
	@OneToMany(mappedBy = "user")
	@Setter(AccessLevel.PRIVATE) 
	private Set<GroupUser> userGroups = new HashSet<GroupUser>();

	@OneToMany(mappedBy = "source")
	@Setter(AccessLevel.PRIVATE) 
	private Set<UserRelation> sourceOfUserRelations = new HashSet<UserRelation>();

	@OneToMany(mappedBy = "target")
	@Setter(AccessLevel.PRIVATE) 
	private Set<UserRelation> targetOfUserRelations = new HashSet<UserRelation>();

	public void addDiscourse(Discourse discourse) {
		this.discourses.add(discourse);
	}
	public void removeDiscourse(Discourse discourse) {
		this.discourses.remove(discourse);
	}
	
	public User(Discourse discourse){
		addDiscourse(discourse);		
	}
}
