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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.annotation.demo.model.BinaryLabeledContributionInterchange;
import edu.cmu.cs.lti.discoursedb.core.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import lombok.extern.log4j.Log4j;

/**
 * Imports the BinaryLabeledContribution interchange format produced by the Exporter.
 * Allows to create and delete annotations (binary labels) from this import file. 
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(	useDefaultFilters = false, 
includeFilters = {@ComponentScan.Filter(
		type = FilterType.ASSIGNABLE_TYPE, 
		value = {ContributionBinaryLabelImporter.class, BaseConfiguration.class })})
public class ContributionBinaryLabelImporter implements CommandLineRunner {
	
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
	
	/**
	 * Launches the SpringBoot application 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length==1,"USAGE: ContributionBinaryLabelImporter <inputFile>");
		SpringApplication.run(ContributionBinaryLabelImporter.class, args);       
	}
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		String inputFileName=args[0];
		
		boolean csv = inputFileName.toLowerCase().endsWith("csv")?true:false;		
		List<BinaryLabeledContributionInterchange> input = csv?fromCsv(inputFileName):fromJson(inputFileName); 
	
		for(BinaryLabeledContributionInterchange item:input){
			Optional<Contribution> existingContrib = contribService.findOne(item.getContribId());
			if(!existingContrib.isPresent()){
				log.error("Contribution with id "+item.getContribId()+" not found. Skipping.");
				continue;
			}else{
				Contribution contrib = existingContrib.get();

				//delete removed labels
				List<AnnotationInstance> toDelete = new ArrayList<>();
				if(contrib.getAnnotations()!=null){
					for(AnnotationInstance anno: contrib.getAnnotations().getAnnotations()){
						if(anno.getType()!=null&&!item.getLabels().contains(anno.getType())){
							toDelete.add(anno);							
						}
					}					
				}				
				annoService.deleteAnnotations(toDelete);

				//add new labels
				for(String label:item.getLabels()){
					//add label as new annotation if it doesn't exist yet
					if(!annoService.hasAnnotationType(contrib, label)){
						annoService.addAnnotation(contrib, annoService.createTypedAnnotation(label));						
					}
				}
			}
		}		
	}
	
	private List<BinaryLabeledContributionInterchange> fromCsv(String inputFileName) throws IOException{
		List<BinaryLabeledContributionInterchange> itemList = new ArrayList<>();
		try(InputStream in = new FileInputStream(inputFileName);) {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(BinaryLabeledContributionInterchange.class);
			MappingIterator<BinaryLabeledContributionInterchange> it = mapper.readerFor(BinaryLabeledContributionInterchange.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				itemList.add(it.next());
			}
		}			
		return itemList;
	}
		
	private List<BinaryLabeledContributionInterchange> fromJson(String inputFileName) throws IOException{
		try(InputStream in = new FileInputStream(inputFileName)) {
			return new ObjectMapper().readValue(in, new TypeReference<List<BinaryLabeledContributionInterchange>>(){});
		}	
	}
}
