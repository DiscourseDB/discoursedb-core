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
package edu.cmu.cs.lti.discoursedb.annotation.demo.io;

import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;

/**
 * Sample Spring Boot application that shows how to retrieve and add annotations (to Contributions and Content)
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(	useDefaultFilters = false, 
				includeFilters = {@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE, 
						value = {SimpleContributionAnnotator.class, BaseConfiguration.class })})
public class SimpleContributionAnnotator implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(SimpleContributionAnnotator.class);	
	
	@Autowired private DiscourseService discourseService;
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
	
	private static String discourseName;
	
	/**
	 * Launches the SpringBoot application 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		if(args.length!=1){
        	throw new IllegalArgumentException("USAGE: SimpleContributionAnnotator <DiscourseName>");
		}
		discourseName = args[0];
        SpringApplication.run(SimpleContributionAnnotator.class, args);       
	}
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
                
		Optional<Discourse> existingDiscourse = discourseService.findOne(discourseName);
		if(!existingDiscourse.isPresent()){
			logger.warn("Discourse with name "+discourseName+" does not exist.");
			return;
		}		
		for(Contribution curContrib: contribService.findAllByDiscourse(existingDiscourse.get())){
			Content curContent = curContrib.getCurrentRevision();
			
			/*
			 * Get existing annotations
			 */
			Set<AnnotationInstance> existingContribAnnos = annoService.findAnnotations(curContrib);
			logger.info(existingContribAnnos.size()+" annotations on contribution");
			
			Set<AnnotationInstance> existingContentAnnos = annoService.findAnnotations(curContent);
			logger.info(existingContentAnnos.size()+" annotations on content");
			
			/*
			 * Create new annotations
			 */
			//for contribution
			AnnotationInstance newContribAnno = annoService.createTypedAnnotation("SampleContributionAnnotation");
			annoService.addFeature(newContribAnno, annoService.createTypedFeature("Feature Value1","Feature Type1")); //feature with value and type
			AnnotationInstance secondContribAnno = annoService.createTypedAnnotation("AnotherContributionAnnotation");
			
			//for content
			AnnotationInstance newContentAnno = annoService.createTypedAnnotation("SampleContentAnnotation");
			annoService.addFeature(newContentAnno,annoService.createTypedFeature("Feature Type2")); // feature with type but no value
			annoService.addFeature(newContentAnno,annoService.createFeature("Feature Value2")); //feature with value but no type			
			
			/*
			 * Annotate: save annotations and link to entities 
			 */
			annoService.addAnnotation(curContrib,newContribAnno);
			annoService.addAnnotation(curContrib,secondContribAnno);
			annoService.addAnnotation(curContent,newContentAnno);
			
		}		
	}
}
