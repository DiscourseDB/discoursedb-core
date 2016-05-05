package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import javax.persistence.EntityNotFoundException;
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

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

/**
 * Compile one text file per DiscoursPart (default type: THREAD) with the concatenated texts of the "currentRevision" Content entities separated
 * by a String which holds meta information about the contribution (table, id, autor name)
 * 
 * Currently, only the latest revision of a contribution is supported.
 * It is assumed that annotations on contirbutions are not spans, but labels on the whole entities
 * while annotations on the content of that contributions are always spans and not entity labels 
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.brat.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratExport.class, BaseConfiguration.class, BratService.class }) })
public class BratExport implements CommandLineRunner {

	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private BratService bratService;	
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: BratDiscourseExport <DiscourseName> <outputFolder> <Split on which DiscoursePart type (default: THREAD)>");
		SpringApplication.run(BratExport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFolder = args[1];
		
		String dpType = "THREAD";
		if(args.length==3){
			dpType=args[2].toUpperCase();
		}

		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourseAndType(discourse, DiscoursePartTypes.valueOf(dpType))){
			//the base filename for this DiscousePart is the name combination of TableName+EntityPrimaryKey, e.g. discourse_part_1
			String baseFileName = dp.getClass().getAnnotation(Table.class).name() + "_"+dp.getId();
			bratService.exportDiscoursePart(dp, outputFolder, baseFileName);
		}	
	}

}
