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
package edu.cmu.cs.lti.discoursedb.user.management;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import edu.cmu.cs.lti.discoursedb.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.service.system.SystemUserService;

/**
 * Manage users from command line
 *
 */
@Component
@Order(1)
public class ManageUsers implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ManageUsers.class);	

	@Autowired private SystemUserService sysUserSvc;
	
	@Override
	public void run(String... args) throws Exception {
		// (list users|list databases|add <user> <password>|delete <user>|grant <user> <database>|revoke <user> <database>|grant public <database>|revoke public <database>)");
		
		if (args[0].equals("list") && args[1].equals("users")) {
			for (SystemUser su: sysUserSvc.getSystemUsers()) {
				System.out.println(su.getUsername());
			}
		}
		if (args[0].equals("list") && args[1].equals("databases")) {
			for (SystemDatabase db: sysUserSvc.getSystemDatabases()) {
				System.out.println(db.getName());
			}
		}
		
	}

}