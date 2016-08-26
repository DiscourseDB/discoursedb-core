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

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * This relation entity associates a contribution c with another contribution or content that represents the context of c.
 * If the context is a content, the begin and end offset can specify a span within the content that constitutes the context.
 * If no span is provided, the whole content is considered to be the context.
 * Alternatively, a contribution ctx can constitute the context of another contribution c.
 * In that case, all content entities associated with ctx are considered to be the context of c.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name = "contribution_has_context", uniqueConstraints = @UniqueConstraint(columnNames = { "fk_contribution",
		"fk_context_contribution", "fk_context_content", "begin_offset", "end_offset" }) )
public class ContributionContext extends TypedTimedBE implements Identifiable<Long>{
	
	@Id
	@Column(name="id_contribution_context", nullable=false)
	@Description("The primary key of this relation.")
    @GeneratedValue(strategy = GenerationType.AUTO)	
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_contribution", nullable=false)
	@Description("A Contribution entity that has another Contribution or Content entity as context.")
    private Contribution contribution;
    
	@ManyToOne(cascade = CascadeType.ALL)
	@Description("A Contribution entity that represents the context of the given contribution.")
	@JoinColumn(name = "fk_context_contribution")
    private Contribution contextContribution;    

	@ManyToOne(cascade = CascadeType.ALL)
	@Description("A Content entity that represents the context of the given contribution. Begin and end offset can further identify a span within the content text or data.")
	@JoinColumn(name = "fk_context_content")
    private Content contextContent;    

	@Column(name="begin_offset")
	@Description("Begin offset that indicates the start index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int beginOffset;
	
	@Column(name="end_offset")
	@Description("End offset that indicates the end index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int endOffset;
		
}
