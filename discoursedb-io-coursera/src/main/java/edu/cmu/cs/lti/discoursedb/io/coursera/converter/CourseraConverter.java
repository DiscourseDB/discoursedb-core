/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 * Contributor Haitian Gong
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
package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.coursera.io.CourseraDB;
import lombok.extern.log4j.Log4j;

/**
 * This converter loads data from a coursera database and maps all entities to DiscourseDB.
 * The DiscourseDB configuration is defined in the dicoursedb-model project and
 * Spring/Hibernate are taking care of connections.
 * 
 * The connection to the coursera database is more lightweight and uses a JDBC
 * connection. The configuration parameters for this connection are passed to
 * the converter as launch parameters in the following order
 * 
 * @author Haitian Gong
 *
 */
@Log4j
@Component
public class CourseraConverter implements CommandLineRunner{
	
	private String dataSetName;
	private String discourseName;
	private String dbhost;
	private String dbname;
	private String dbuser;
	private String dbpwd;
	
	@Autowired 
	CourseraConverterService converterService;
	
	@Override
	public void run(String... args) throws Exception {	
		Assert.isTrue(args.length==6,"Usage: CourseraConverterApplication <DataSetName> <DiscourseName> <coursera_dbhost> <coursera_db> <coursera_dbuser> <coursera_dbpwd>");
		
		this.dataSetName = args[0];
		this.discourseName = args[1];
		this.dbhost = args[2];
		this.dbname = args[3];
		this.dbuser = args[4];
		this.dbpwd = args[5];

		log.info("Starting coursera conversion");
		convert();
		log.info("Coursera conversion completed");
	}
	
	private void convert() throws SQLException {
		
		CourseraDB database = new CourseraDB(
				this.dbhost, this.dbname, this.dbuser, this.dbpwd);
		
		//Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		
		//Phase 1: read through forum data from database and map all entities
		converterService.mapForum(database, dataSetName, discourseName);
		
		//Phase 2: read through thread data from database and map all entities
		converterService.mapThread(database, dataSetName, discourseName);
		
		//Phase 3: read through post data from database and map all entities
		converterService.mapPost(database, dataSetName, discourseName);
		
		//Phase 4: read through comment data from database and map all entities
		converterService.mapComment(database, dataSetName, discourseName);
	}

}
