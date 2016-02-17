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
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
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
@ComponentScan(	basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.demo.io" }, 
useDefaultFilters = false, 
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
		boolean csv = false;
		if(inputFileName.toLowerCase().endsWith("csv")){
			csv=true;
		}
		
		List<BinaryLabeledContributionInterchange> input;
		if(csv){
			input = fromCsv(inputFileName);
		}else{
			input = fromJson(inputFileName);			
		}
	
		for(BinaryLabeledContributionInterchange item:input){
			Optional<Contribution> existingContrib = contribService.findOne(item.getId());
			if(!existingContrib.isPresent()){
				log.error("Contribution with id "+item.getId()+" not found. Skipping.");
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
