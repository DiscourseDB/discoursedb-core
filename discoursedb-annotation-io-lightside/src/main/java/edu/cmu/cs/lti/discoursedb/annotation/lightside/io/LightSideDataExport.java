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
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: LightSideDataExport <DiscourseName> <outputFile> <DiscoursePart type to extract (default: THREAD)>");
		SpringApplication.run(LightSideDataExport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		String outputFilePath = args[1];
		Assert.hasText(outputFilePath, "Path to the output file cannot be empty.");		
		
		File outputFile = new File(outputFilePath);
		Assert.isTrue(!outputFile.isDirectory(), outputFilePath+" points to a Directory but should point to a file.");
		
		DiscoursePartTypes dptype = DiscoursePartTypes.THREAD;
		if(args.length==3){
			dptype = DiscoursePartTypes.valueOf(args[2]);
		}
		
		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourseAndType(discourse, dptype)){
			lsService.exportDataForAnnotation(outputFilePath, dp);
		}			
	}
}
