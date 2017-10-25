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
package edu.cmu.cs.lti.discoursedb.sandbox;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.AnnotationRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

/**
 * 1. We need a ComponentScan in order to discover the configuration
 * 
 * 2. We need to wrap everything in a transaction in order to allow lazy loading
 * to work. If we didn't do that, than we would have a single transaction for
 * each interaction with a data repository. However, the transaction does not
 * extend to the proxy objects retrieved in lazy loading
 * 
 * @author Oliver Ferschke
 *
 */
@Transactional
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class TestAnnotation implements CommandLineRunner {

	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contribService;	
	@Autowired private ContentService contentService;	
	@Autowired private AnnotationService annoService;

	public static void main(String[] args) {
		SpringApplication.run(TestAnnotation.class);
	}

	@Override
	public void run(String... strings) throws Exception {
		Discourse d = discourseService.createOrGetDiscourse("AnnoTestDiscourse", "test1");
		
		DiscoursePart dp = discoursePartService.createTypedDiscoursePart(d, DiscoursePartTypes.FORUM);

		Contribution contrib1 = contribService.createTypedContribution(ContributionTypes.POST);
		Content content1 = contentService.createContent();
		content1.setText("This sentence is spread");
		contrib1.setFirstRevision(content1);
		contrib1.setCurrentRevision(content1);
		discoursePartService.addContributionToDiscoursePart(contrib1, dp);

		Contribution contrib2 = contribService.createTypedContribution(ContributionTypes.POST);
		Content content2 = contentService.createContent();
		content2.setText("across two contributions.");
		contrib1.setFirstRevision(content2);		
		contrib1.setCurrentRevision(content2);
		
		discoursePartService.addContributionToDiscoursePart(contrib2, dp);
		
		AnnotationInstance anno1 = annoService.createTypedAnnotation("TestAnnoType");		
		anno1.setBeginOffset(0);
		anno1.setEndOffset(content1.getText().length());
		annoService.addAnnotation(content1, anno1);
		
		AnnotationInstance anno2 = annoService.createTypedAnnotation("TestAnnoType");
		anno2.setBeginOffset(0);
		anno2.setEndOffset(content2.getText().length());
		annoService.addAnnotation(content2, anno2);
	
		annoService.createAnnotationRelation(anno1, anno2, AnnotationRelationTypes.MULTI_ENTITY_ANNOTATION, true);

	}

}
