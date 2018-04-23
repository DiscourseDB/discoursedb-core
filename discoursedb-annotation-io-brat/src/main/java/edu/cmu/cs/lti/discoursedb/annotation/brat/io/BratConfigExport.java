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
package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.configuration.BaseConfiguration;

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

	@Autowired private BratService bratService;
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=1 && args.length <=2, "USAGE: BratConfigExport (<DiscourseName>) <outputFolder> --jdbc.database=<database name>");
		SpringApplication.run(BratConfigExport.class, args);
	}

	@Override
	public void run(String... args) throws Exception {		
		if(args.length==1){
			bratService.generateBratConfig(args[0]);			
		}else{
			bratService.generateBratConfig(args[0], args[1]);			
		}
		
	}

}
