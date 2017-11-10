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
package edu.cmu.cs.lti.discoursedb.sandbox;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

/**
 * 1. We need a ComponentScan in order to discover the configuration
 * 
 * 2. We need to wrap everything in a transaction in order to allow lazy loading
 * to work. If we didn't do that, than we would have a single transaction for
 * each interaction with a data repository. However, the transaction does not
 * extend to the proxy objects retrieved in lazy loading
 * 
 * @author Oliver Ferschke
 *
 */
@Transactional
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class TestSpringDataJPA implements CommandLineRunner {

	@Autowired
	private UserService userService;

	@Autowired
	private DiscourseService discourseService;

	@Autowired
	private DataSourceService dataSourceService;

	public static void main(String[] args) {
		SpringApplication.run(TestSpringDataJPA.class);
	}

	@Override
	public void run(String... strings) throws Exception {
		Discourse d = discourseService.createOrGetDiscourse("UTArlingtonX/LINK5.10x/3T2014");
		User u = userService.createOrGetUser(d, "olifer");
		dataSourceService.addSource(u, new DataSourceInstance("testSourceId","testSourceIdDescriptor",DataSourceTypes.EDX,"edxtestdataset"));
		
//		for(User x:userService.findUsersBySourceIdAndDataSetName("testSourceId","")){
//			System.out.println(x.getUsername());
//		}
//		u.setRealname("Oliver Ferschke");
//		userService.delete(u);
		
	}

}
