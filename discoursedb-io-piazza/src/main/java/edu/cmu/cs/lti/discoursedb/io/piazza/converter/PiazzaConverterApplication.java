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
package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;


/**
 * This starter class launches the components necessary for importing piazza discussion data
 * from several json dumps into DiscourseDB.
 * 
 * This starter class requires three mandatory parameters: DiscourseName DataSetName piazzaDumpPath
 *
 * The piazzaDumpPath is the path for one piazza dump file (in json format).
 * 
 * 
 * @author Oliver Ferschke
 * @author Haitian Gong
 *
 */
@Configuration
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.piazza.converter"})
public class PiazzaConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName   the name of the dicourse
	 *     DataSetName     the name of the dataset
	 *     piazzaDumpPath  the path of the piazza JSON dump file
	 */
	
	public static void main(String... args) {
		Assert.isTrue(args.length==3, "Usage: PiazzaConverterApplication <DiscourseName> <DatasetName> <piazza json dump>");		
		SpringApplication.run(PiazzaConverterApplication.class, args);		
	}

}
