package edu.cmu.cs.lti.discoursedb.annotation.demo.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Table;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.annotation.demo.model.BinaryLabelInterchange;
import edu.cmu.cs.lti.discoursedb.annotation.demo.model.BinaryLabeledContributionInterchange;
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;

/**
 * This class exports annotations on Contribution entities for a given discourse. The
 * annotations in the json can be edited offline or by a third party software
 * and then be imported back into the DiscourseDB database.
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(	basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.demo.io" }, 
				useDefaultFilters = false, 
				includeFilters = {@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE, 
						value = {ContributionBinaryLabelExporter.class, BaseConfiguration.class })})
public class ContributionBinaryLabelExporter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ContributionBinaryLabelExporter.class);	
	
	@Autowired private DiscourseService discourseService;
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
	
	private static String discourseName;
	private static String outputFileName;
	
	/**
	 * Launches the SpringBoot application 
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		if(args.length!=2){
        	throw new IllegalArgumentException("USAGE: SimpleJSONContentAnnotationExporter <DiscourseName> <outputFile>");
		}
		discourseName = args[0];
		outputFileName=args[1];
        SpringApplication.run(ContributionBinaryLabelExporter.class, args);       
	}
	
	@Override
	@Transactional
	public void run(String... args) throws Exception {
		List<BinaryLabelInterchange> output = new ArrayList<>();		
		Optional<Discourse> existingDiscourse = discourseService.findOne(discourseName);
		ObjectMapper mapper = new ObjectMapper();
		if(!existingDiscourse.isPresent()){
			logger.warn("Discourse with name "+discourseName+" does not exist.");
			return;
		}		

		for(Contribution contrib: contribService.findAllByDiscourse(existingDiscourse.get())){
			BinaryLabeledContributionInterchange curAnnoExport = new BinaryLabeledContributionInterchange();			
			curAnnoExport.setTable(contrib.getClass().getAnnotation(Table.class).name()); //table name automatically determined
			curAnnoExport.setId(contrib.getId());
			curAnnoExport.setText(contrib.getFirstRevision().getText());
			for(AnnotationInstance anno:annoService.findAnnotations(contrib)){
				if(anno.getType()!=null){
					curAnnoExport.addLabel(anno.getType().getType());					
				}
			}
			output.add(curAnnoExport);
		}

		mapper.writeValue(new File(outputFileName),output);
		
	}
}
