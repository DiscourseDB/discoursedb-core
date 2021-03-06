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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableSourcedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

/**
 * A DiscoursePart represents a distinct sub-space within a Discourse. For
 * instance, a DiscoursePart could represent a discussion forum. That is, it
 * acts as a container for interactions that happen in this discussion forum.
 * DiscourseParts are typed entities, i.e. they are associated with a
 * DiscoursePartType which indicates what the DiscoursePart represents, e.g. a
 * {@link edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes#FORUM}. 
 * 
 * Furthermore, DiscourseParts can be related to each other with DiscoursePartRelations in order to indicate embedded structures. 
 * For instance, a forum could consist of multiple sub-forums. 
 * 
 * DiscoursePartRelations are also typed entities, i.e. they are related to a DiscoursePartRelationType indicating what the relation represents, 
 * e.g. an EMBEDDING in the case of forum-subforum.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true, exclude={"discourseToDiscourseParts","discoursePartContributions","sourceOfDiscoursePartRelations","targetOfDiscoursePartRelations"})
@ToString(callSuper=true, exclude={"discourseToDiscourseParts","discoursePartContributions","sourceOfDiscoursePartRelations","targetOfDiscoursePartRelations"})
@Entity
@Table(name="discourse_part")
public class DiscoursePart extends TypedTimedAnnotatableSourcedBE implements Identifiable<Long> {

	@Id
	@Column(name="id_discourse_part", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@Column(columnDefinition="TEXT")
	private String name;
	
    @OneToMany(mappedBy = "discoursePart")
	@Setter(AccessLevel.PRIVATE) 
	private Set<DiscourseToDiscoursePart> discourseToDiscourseParts = new HashSet<DiscourseToDiscoursePart>();

    @OneToMany(mappedBy = "discoursePart")
	@Setter(AccessLevel.PRIVATE) 
	private Set<DiscoursePartContribution> discoursePartContributions = new HashSet<DiscoursePartContribution>();
	
    @OneToMany(mappedBy="source")
	@Setter(AccessLevel.PRIVATE) 
	private Set<DiscoursePartRelation> sourceOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();

    @OneToMany(mappedBy="target")
	@Setter(AccessLevel.PRIVATE) 
	private Set<DiscoursePartRelation> targetOfDiscoursePartRelations = new HashSet<DiscoursePartRelation>();
	
}
