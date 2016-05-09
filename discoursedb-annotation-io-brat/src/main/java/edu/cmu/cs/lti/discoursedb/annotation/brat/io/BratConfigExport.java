package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.FileUtils;
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
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;

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
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratConfigExport.class, BaseConfiguration.class, BratService.class }) })
public class BratConfigExport implements CommandLineRunner {

	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private AnnotationService annoService;
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length ==2, "USAGE: BratConfigExport <DiscourseName> <outputFolder>");
		SpringApplication.run(BratConfigExport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFolder = args[1];
		
		Set<String> annoTypes = new HashSet<>();
		List<String> annotationConf = new ArrayList<>();
		
		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));		
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourse(discourse)){
			annoTypes.addAll(annoService.findContributionAnnotationsByDiscoursePart(dp).stream().map(anno->anno.getType()).collect(Collectors.toSet()));
			annoTypes.addAll(annoService.findCurrentRevisionAnnotationsByDiscoursePart(dp).stream().map(anno->anno.getType()).collect(Collectors.toSet()));
		}	
		
		annotationConf.add("[relations]");
		annotationConf.add("[events]");
		annotationConf.add("[attributes]");
		annotationConf.add("[entities]");
		annotationConf.addAll(annoTypes);
		
		FileUtils.writeLines(new File(outputFolder,"annotation.conf"), annotationConf);
	}

}
