/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.scheduling.annotation.EnableAsync;

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratConfigExport;
import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratExport;
import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratImport;
import edu.cmu.cs.lti.discoursedb.annotation.brat.sandbox.TestDiscourseGenerator;
import edu.cmu.cs.lti.discoursedb.annotation.brat.util.UnannotatedContributionRemover;
import edu.cmu.cs.lti.discoursedb.annotation.lightside.io.LightSideDataExport;
import edu.cmu.cs.lti.discoursedb.annotation.lightside.io.LightSideDataImport;
import edu.cmu.cs.lti.discoursedb.annotation.lightside.io.LightSideTrainingDataExport;

/**
 * A SpringBootApplication that launches a server that hosts the API.
 *
 */
@SpringBootApplication
@EnableEntityLinks
@EnableAsync
@EntityScan(basePackageClasses = { DiscourseApiStarter.class, Jsr310JpaConverters.class })
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" }, excludeFilters = {
		  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, 
				  value = { UnannotatedContributionRemover.class, 
						  TestDiscourseGenerator.class, BratConfigExport.class, BratExport.class, BratImport.class,
						  LightSideDataExport.class, LightSideDataImport.class, LightSideTrainingDataExport.class }) })
public class DiscourseApiStarter {

	public static void main(String[] args) {
		
		SpringApplication.run(DiscourseApiStarter.class, args); 
	}

	
}
