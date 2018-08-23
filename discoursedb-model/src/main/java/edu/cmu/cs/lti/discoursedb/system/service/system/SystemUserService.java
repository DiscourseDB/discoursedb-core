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
package edu.cmu.cs.lti.discoursedb.system.service.system;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.mysql.jdbc.Connection;

import edu.cmu.cs.lti.discoursedb.core.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemDatabaseRepository;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserProperty;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserRight;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class SystemUserService {

	private final @NonNull SystemUserRepository sysUserRepo;
	private final @NonNull SystemDatabaseRepository sysDbRepo;
	//private final @NonNull DatabaseSelector selector;
	
	public Optional<SystemUser> getSystemUser(String systemUserName) {
		Assert.hasText(systemUserName, "System user name must not be empty");
		return sysUserRepo.findOneByUsername(systemUserName);
	}

	public Optional<SystemUser> getSystemUser() {
		
		Authentication auth =  SecurityContextHolder.getContext().
				getAuthentication();
		if (auth != null && auth.getPrincipal() != null) {
			return this.getSystemUser(auth.getPrincipal().toString());
		} else {
			return Optional.empty();
		}
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public SystemUser findOrCreateSystemUser(String email, String name, String username) {
		Optional<SystemUser> su = getSystemUser(username);
		if (su.isPresent()) {
			return su.get();
		} else {
			return createSystemUser(email,name,username);
		}
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public SystemUser findOrCreateSystemUserByUsername(String email, String name, String username, String password) {
		Optional<SystemUser> su = getSystemUser(username);
		if (su.isPresent()) {
			su.get().setPasswordHash(passwordEncoder().encode(password));
			sysUserRepo.save(su.get());
			return su.get();
		} else {
			SystemUser su2 =createSystemUser(email,name,username);
			su2.setPasswordHash(passwordEncoder().encode(password));
			sysUserRepo.save(su2);
			return su2;
		}
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public SystemUser findOrCreateSystemUserByEmail(String email, String name, String username, String password) {
		Optional<SystemUser> su = findUserByEmail(email);
		if (su.isPresent()) {
			su.get().setPasswordHash(passwordEncoder().encode(password));
			sysUserRepo.save(su.get());
			return su.get();
		} else {
			SystemUser su2 =createSystemUser(email,name,username);
			su2.setPasswordHash(passwordEncoder().encode(password));
			sysUserRepo.save(su2);
			return su2;
		}
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public void setPassword(SystemUser su, String password) {
		su.setPasswordHash(passwordEncoder().encode(password));
		sysUserRepo.save(su);
	}
	
	public boolean checkIfDatabaseExists(String dbname) {
		// Connection connection = <your java.sql.Connection>
		Session session = sem.unwrap(Session.class);
		boolean found =  session.doReturningWork(connection -> {
			ResultSet resultSet = connection.getMetaData().getCatalogs();
			boolean ifound = false;
			while (resultSet.next() && ifound==false) {
			  // Get the database name, which is at position 1
			  String databaseName = resultSet.getString(1);
			  if (databaseName.replaceAll("discoursedb_ext_","").equals(dbname.replaceAll("discoursedb_ext_", ""))) {
				  ifound = true;
			  }
			}
			resultSet.close();
			return ifound;
		});
		
		return found;
	}
	
	public SystemUser createSystemUser(String email, String name, String username) {
		SystemUser newu = new SystemUser();
		newu.setEmail(email);
		newu.setRealname(name);
		newu.setUsername(email);
		return sysUserRepo.save(newu);
	}
	public List<SystemUser> getSystemUsers() {
		return sysUserRepo.findAll();
	}
	public Set<SystemDatabase> getSystemDatabases() {
		return sysDbRepo.findAll();
	}
	public Optional<SystemDatabase> databaseExists(String dbname) {
		return sysDbRepo.findOneByName(dbname);
	}
	
	public List<SystemUserProperty> getPropertyList() {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		// TODO: do we need to substitute * -> %  in ptype to make "like" work inside findProperties?
		// TODO: do we need to sanitize ptype?  Can it bust out of the string and do '"; drop TABLES;' or whatever?
		return new ArrayList<SystemUserProperty>(su.get().getProperties());
		//return sysUserRepo.findProperties(su.get(), ptype);
	}
	
	// Get for a different user
	public List<SystemUserProperty> getPropertyList(SystemUser su) {
		// TODO: do we need to substitute * -> %  in ptype to make "like" work inside findProperties?
		// TODO: do we need to sanitize ptype?  Can it bust out of the string and do '"; drop TABLES;' or whatever?
		return new ArrayList<SystemUserProperty>(su.getProperties());
		//return sysUserRepo.findProperties(su.get(), ptype);
	}
	
	public Optional<SystemUserProperty> getProperty(String ptype, String pname) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		Assert.doesNotContain(ptype, "*","Can't get property with wildcard type");
		Assert.doesNotContain(ptype, "%","Can't get property with wildcard type");
		return sysUserRepo.getProperty(su.get(), ptype, pname);
	}
	
	@Transactional(value="systemTransactionManager", propagation = Propagation.REQUIRED)
	public int deleteProperty(String ptype, String pname) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		Assert.doesNotContain(ptype, "*","Can't get property with wildcard type");
		Assert.doesNotContain(ptype, "%","Can't get property with wildcard type");
		return sysUserRepo.deleteProperty(su.get(), ptype, pname);
	}
	
	
	public Optional<SystemUserProperty> renameProperty(String ptype, String pname, String newPname, String newPtype) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		Assert.doesNotContain(ptype, "*","Can't get property with wildcard type");
		Assert.doesNotContain(ptype, "%","Can't get property with wildcard type");
		Optional<SystemUserProperty> old = getProperty(ptype, pname);
		if (!old.isPresent()) { return old; }
		Optional<SystemUserProperty> nuevo = getProperty(newPtype, newPname);
		if (nuevo.isPresent()) { return Optional.empty(); }
		deleteProperty(ptype, pname);
		createProperty(newPtype, newPname, old.get().getPropValue());
		return getProperty(newPname, newPtype);
	}
	@Autowired @Qualifier("systemEntityManagerFactory") public EntityManager sem;
	//@Autowired @Qualifier("coreEntityManagerFactory") public EntityManager cem;
	@Transactional(value="systemTransactionManager", propagation = Propagation.REQUIRED)
	public int createProperty(String ptype, String pname, String pvalue) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		sysUserRepo.deleteProperty(su.get(), ptype,  pname);
		int retval = sysUserRepo.createProperty(ptype, pname, pvalue, su.get().getId());
		return retval;
	}

	public void refreshSystemDatabase() {
		sem.clear();
		/*for (Object key: selector.listOpenDatabases()) {
			selector.changeDatabase((String)key);
			cem.clear();
		}*/
	}

	public boolean registerDatabase(String dbname) {
		
		Optional<SystemDatabase> sd = sysDbRepo.findOneByName(dbname);
		if (!sd.isPresent()) {
			SystemDatabase newsd = new SystemDatabase();
			newsd.setName(dbname);
			newsd.setIsPublic(0);
			sysDbRepo.save(newsd);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean setDatabasePublic(String dbname, int isPublic) {
		Optional<SystemDatabase> sd = sysDbRepo.findOneByName(dbname);
		if (sd.isPresent()) {
			sd.get().setIsPublic(isPublic);
			return true;
		} else {
			return false;
		}
	}

	public boolean unregisterDatabase(String dbname) {
		Optional<SystemDatabase> sd = sysDbRepo.findOneByName(dbname);
		if (!sd.isPresent()) {
			return false;
		} else {
			sysDbRepo.delete(sd.get());
			return true;
		}
	}

	public boolean deleteUserByEmail(String email) {
		Optional<SystemUser> su = sysUserRepo.findOneByEmail(email);
		if (!su.isPresent()) {
			return false;
		} else {
			sysUserRepo.delete(su.get());
			return true;
		}
	}

	
	public boolean grantDatabaseRight(SystemUser su, String dbname) {
		if (sysUserRepo.checkRight(su, dbname).size() == 0) {
			Optional<SystemDatabase> sd = sysDbRepo.findOneByName(dbname);
			if (sd.isPresent()) {
				sysUserRepo.grantAccess(dbname, su.getId());
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Optional<SystemUser> findUserByEmail(String email) {
		return sysUserRepo.findOneByEmail(email);
	}

	public boolean revokeDatabaseRight(SystemUser su, String dbname) {
		if (sysUserRepo.checkRight(su, dbname).size() > 0) {
			sysUserRepo.revokeAccess(dbname, su.getId());
			return true;
		}
		return false;
	}
	
	
}
