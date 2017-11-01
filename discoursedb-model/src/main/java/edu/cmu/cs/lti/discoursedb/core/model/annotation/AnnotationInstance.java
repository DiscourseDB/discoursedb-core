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
package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedSourcedBE;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotationEntityProxy"})
@ToString(callSuper=true, exclude={"annotationEntityProxy"})
@Entity
@Table(name="annotation_instance")
@Description("A single instance of an annotation")
public class AnnotationInstance extends TypedSourcedBE implements Identifiable<Long>{
    
	@Id
	@Column(name="id_annotation_instance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("Primary key")
	private Long id;
	
	@Column(name="begin_offset")
	@Description("Begin offset that indicates the start index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int beginOffset;
	
	@Column(name="end_offset")
	@Description("End offset that indicates the end index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int endOffset;
	
	@Column(name="covered_text", columnDefinition="TEXT")
	@Description("The text between begin_offset and end_offset.")
	private String coveredText;
		
	@ManyToOne 
	@JoinColumn(name = "fk_annotation_entity_proxy")
	@Description("The aggregate entity that aggregates all annotations belonging to the associated/annotated entity.")
	private AnnotationEntityProxy annotationEntityProxy;
	
	/*
	 * 
	 * @ManyToOne 
	 *
	@JoinColumn(name = "fk_annotator")
	@Description("The user who created this annotation instance.")
	private SystemUser annotator;
	*/
	
	@Column(name="annotator_email")
	@Description("The user who created this annotation instance.")
	private String annotatorEmail;
	
	@Transient
	public String getEmail() { return annotatorEmail; }
	@Transient
	public void setAnnotator(SystemUser anno) { annotatorEmail = anno.getEmail(); }
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="annotation")
	@Description("A set of features that represent the payload of this annotation.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<Feature> features = new HashSet<Feature>();
	
    @OneToMany(mappedBy="source")
	@Description("A set of relations that associate this annotation with other annotations. This set contains only those relations of which the present annotation is the source.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AnnotationRelation> sourceOfAnnotationRelations = new HashSet<AnnotationRelation>();

    @OneToMany(mappedBy="target")
	@Description("A set of relations that associate this annotation with other annotations. This set contains only those relations of which the present annotation is the target.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AnnotationRelation> targetOfAnnotationRelations = new HashSet<AnnotationRelation>();
		
}
