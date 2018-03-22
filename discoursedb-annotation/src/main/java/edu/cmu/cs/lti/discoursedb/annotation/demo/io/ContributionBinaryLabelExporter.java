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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import edu.cmu.cs.lti.discoursedb.annotation.demo.model.BinaryLabeledContributionInterchange;
import edu.cmu.cs.lti.discoursedb.core.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.extern.log4j.Log4j;

/**
 * This class exports annotations on Contribution entities for a given discourse. 
 * The annotations in the json/csv can be edited offline or by a third party software
 * and then be imported back into the DiscourseDB database using the importer class.
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(	useDefaultFilters = false, 
				includeFilters = {@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE, 
						value = {ContributionBinaryLabelExporter.class, BaseConfiguration.class })})
public class ContributionBinaryLabelExporter implements CommandLineRunner {
	
	@Autowired private DiscourseService discourseService;
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
		
	/**
	 * Launches the SpringBoot application 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length==2,"USAGE: SimpleJSONContentAnnotationExporter <DiscourseName> <outputFile> <csv>(optional)");
        SpringApplication.run(ContributionBinaryLabelExporter.class, args);       
	}
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFileName=args[1];
		boolean csv = outputFileName.toLowerCase().endsWith("csv")?true:false;

		List<BinaryLabeledContributionInterchange> output = new ArrayList<>();		
		Optional<Discourse> existingDiscourse = discourseService.findOne(discourseName);		
		
		if(!existingDiscourse.isPresent()){
			log.warn("Discourse with name "+discourseName+" does not exist.");
			return;
		}		

		//retrieve all contributions for the given discourse
		for(Contribution contrib: contribService.findAllByDiscourse(existingDiscourse.get())){
			
			//wrap all relevant information about the given contribution in an interchange object
			BinaryLabeledContributionInterchange curAnnoExport = new BinaryLabeledContributionInterchange();			
			curAnnoExport.setTable(contrib.getClass().getAnnotation(Table.class).name()); //table name automatically determined
			curAnnoExport.setContribId(contrib.getId());
			curAnnoExport.setText(contrib.getCurrentRevision().getText());
			curAnnoExport.setContribType(contrib.getType());

			for(DiscoursePartContribution dpc:contrib.getContributionPartOfDiscourseParts()){
				curAnnoExport.addThreadId(dpc.getDiscoursePart().getId());
			}
			
			for(AnnotationInstance anno:annoService.findAnnotations(contrib)){
				if(anno.getType()!=null){
					curAnnoExport.addLabel(anno.getType());					
				}
			}
			//add interchange object to export list
			output.add(curAnnoExport);
		}

		//write all interchange objects to output file
		if(csv){toCSV(output,outputFileName);}else{toJSON(output,outputFileName);}

	}

	private void toJSON(Collection<?> data, String outputFileName) throws IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(new File(outputFileName),data);
	}
	
	private void toCSV(Collection<?> data, String outputFileName) throws IOException{
		
		CsvMapper mapper = new CsvMapper();
		String[] header = BinaryLabeledContributionInterchange.class.getAnnotation(JsonPropertyOrder.class).value();
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName)))) {
			out.write(mapper.writeValueAsString(header));
			for(Object a:data){
				out.write(mapper.writerWithSchemaFor(a.getClass()).writeValueAsString(a));
			}	    	
	    }		
	}
}
