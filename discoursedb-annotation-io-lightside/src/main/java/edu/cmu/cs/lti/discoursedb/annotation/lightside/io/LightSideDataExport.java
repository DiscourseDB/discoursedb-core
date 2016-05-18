package edu.cmu.cs.lti.discoursedb.annotation.lightside.io;

import java.io.File;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

/**
 * Exports data to LightSide so it can be annotated with a trained model. 
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.lightside.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { LightSideDataExport.class, BaseConfiguration.class, LightSideService.class }) })
public class LightSideDataExport implements CommandLineRunner{

	@Autowired private LightSideService lsService;	
	@Autowired private DiscoursePartService discoursePartService;	
	@Autowired private DiscourseService discourseService;	
	
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: LightSideDataExport <DiscourseName> <outputFolder> <DiscoursePart type to extract (default: THREAD)>");
		SpringApplication.run(LightSideDataExport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		String outputFolderPath = args[1];
		Assert.hasText(outputFolderPath, "Path to the output directory cannot be empty.");		
		
		File outputFolder = new File(outputFolderPath);
		Assert.isTrue(outputFolder.isDirectory(), outputFolderPath+" is not a directory.");
		
		DiscoursePartTypes dptype = DiscoursePartTypes.THREAD;
		if(args.length==3){
			dptype = DiscoursePartTypes.valueOf(args[2]);
		}
		
		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourseAndType(discourse, dptype)){
			lsService.exportDataForAnnotation(outputFolderPath, dp);
		}			
	}
}
