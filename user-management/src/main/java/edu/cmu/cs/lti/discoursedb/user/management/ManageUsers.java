/*******************************************************************************
 * Copyright (C)  2018  Carnegie Mellon University
 * Author: Chris Bogart
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


import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserRight;
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
	
		// list users
		// list databases
		// add <user-email> <real-user-name> <userid> <password>
		// delete <user-email>
		// grant <user-email> <database>
		// revoke <user-email> <database>
		// register <database>
		// unregister <database>
		// grant public <database>
		// revoke public <database>");
		
		if (args[0].equals("list") && args[1].equals("users")) {
			for (SystemUser su: sysUserSvc.getSystemUsers()) {
				System.out.println("Email:" + su.getEmail() + "\t Full name:" + su.getRealname() + "\t Username:" + su.getUsername());
				System.out.println("     Can access all public databases, plus:");
				for(SystemUserRight sur: su.getRights()) {
					System.out.println("            " + sur.getDatabaseName());
				}
			}
		}
		else if (args[0].equals("list") && args[1].equals("databases")) {
			for (SystemDatabase db: sysUserSvc.getSystemDatabases()) {
				System.out.println(db.getName());
			}
		}
		else if (args[0].equals("password") && args.length==3) {
			try {
				Optional<SystemUser> su = sysUserSvc.findUserByEmail(args[1]);
				sysUserSvc.setPassword(su.get(), args[2]);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		else if (args[0].equals("add") && args.length == 5) {
				try {
					sysUserSvc.findOrCreateSystemUserByEmail(args[1],args[2],args[3], args[4]);
					
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
			
		}
		else if (args[0].equals("delete") && args.length == 2) {
			if (sysUserSvc.deleteUserByEmail(args[1])) {
				System.out.println("Deleted " + args[1]);
			} else {
				System.out.println(args[1] + " not found");
			}
		}
		else if (args[0].equals("register") && args.length == 2) {
			if (!sysUserSvc.checkIfDatabaseExists(args[1])) {
				System.out.println("Database " + "discoursedb_ext_" + args[1].replaceAll("discoursedb_ext_", "") + " not found");
			} else if (sysUserSvc.registerDatabase(args[1])) { 
				System.out.println("Registered " + args[1]);
			} else {
				System.out.println(args[1] + " already registered");
			}
		}
		else if (args[0].equals("unregister") && args.length == 2) {
			if (sysUserSvc.unregisterDatabase(args[1])) { 
				System.out.println("Unregistered " + args[1]);
			} else {
				System.out.println(args[1] + " not found");
			}
		}
		
		else if (args[0].equals("grant") && !args[1].equals("public") && args.length == 3) {
			try {
				Optional<SystemUser> su = sysUserSvc.findUserByEmail(args[1]);
				if (sysUserSvc.grantDatabaseRight(su.get(), args[2])) {
					System.out.println("Granted " + args[2] + " to " + args[1]);
				} else {
					if (!sysUserSvc.checkIfDatabaseExists(args[2])) {
						System.out.println( "Database " + args[2] + " not found");
					} else {
						System.out.println("Cannot grant " + args[2] + " to " + args[1]);
					}
				}
				
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		else if (args[0].equals("grant") && args[1].equals("public") && args.length == 3) {
			try {
				if (sysUserSvc.setDatabasePublic(args[2], 1)) {
					System.out.println(args[2] + " made public");
				} else {
					System.out.println(args[2] + " not found");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		else if (args[0].equals("revoke") && !args[1].equals("public") && args.length == 3) {
			try {
				Optional<SystemUser> su = sysUserSvc.findUserByEmail(args[1]);
				if (sysUserSvc.revokeDatabaseRight(su.get(), args[2])) {
					System.out.println("Revoked " + args[2] + " to " + args[1]);
				} else {
					System.out.println(args[1] + " does not have access to " + args[2]);
				}
				
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
		else if (args[0].equals("revoke") && args[1].equals("public") && args.length == 3) {
			try {
				if (sysUserSvc.setDatabasePublic(args[2], 0)) {
					System.out.println(args[2] + " made private");
				} else {
					System.out.println(args[2] + " not found");
				}
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		} else {
			System.out.println("Error: Did not recognize command " + String.join(" ",  args));
		}
	
	}
	
}