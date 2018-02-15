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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemDatabaseRepository;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserProperty;
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
	private final @NonNull DatabaseSelector selector;
	
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
	
	
	public SystemUser createSystemUser(String email, String name, String username) {
		SystemUser newu = new SystemUser();
		newu.setEmail(email);
		newu.setRealname(name);
		newu.setUsername(email);
		return sysUserRepo.save(newu);
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
	@Autowired @Qualifier("coreEntityManagerFactory") public EntityManager cem;
	@Transactional(value="systemTransactionManager", propagation = Propagation.REQUIRED)
	public int createProperty(String ptype, String pname, String pvalue) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		sysUserRepo.deleteProperty(su.get(), ptype,  pname);
		int retval = sysUserRepo.createProperty(ptype, pname, pvalue, su.get().getId());
		return retval;
	}

	public void refreshOpenDatabases() {
		sem.clear();
		for (Object key: selector.listOpenDatabases()) {
			selector.changeDatabase((String)key);
			cem.clear();
		}
	}
	
	
}
