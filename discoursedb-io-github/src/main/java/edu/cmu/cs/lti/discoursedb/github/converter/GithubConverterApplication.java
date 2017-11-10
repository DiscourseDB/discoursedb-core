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
package edu.cmu.cs.lti.discoursedb.github.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot starter class which launches all components that can be found in any sub-package of <code>edu.cmu.cs.lti.discoursedb.github.converter</code><br/>
 * The args of the main methods will be passed on to all components that implement the CommandLineRunner interface.<br/>
 * 
 * Not much else is needed in this class. If any parameters are required, it would be good practice to validate them here.
 * 
 * @author Oliver Ferschke
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.core", "edu.cmu.cs.lti.discoursedb.system","edu.cmu.cs.lti.discoursedb.github.converter"})
public class GithubConverterApplication {
	
	public static void main(String[] args) {
        SpringApplication.run(GithubConverterApplication.class, args);       
	}
}
