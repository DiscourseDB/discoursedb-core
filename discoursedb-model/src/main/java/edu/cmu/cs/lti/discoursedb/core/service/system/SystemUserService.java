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
package edu.cmu.cs.lti.discoursedb.core.service.system;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.core.model.system.SystemUserProperty;
import edu.cmu.cs.lti.discoursedb.core.repository.system.SystemUserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class SystemUserService {

	private final @NonNull SystemUserRepository sysUserRepo;
	
	public Optional<SystemUser> getSystemUser(String systemUserName) {
		Assert.hasText(systemUserName, "System user name must not be empty");
		return sysUserRepo.findOneByUsername(systemUserName);
	}

	public Optional<SystemUser> getSystemUser() {
		return this.getSystemUser(SecurityContextHolder.getContext().
				getAuthentication().getPrincipal().toString());
	}
	
	public List<SystemUserProperty> getPropertyList(String ptype) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		// TODO: do we need to substitute * -> %  in ptype to make "like" work inside findProperties?
		// TODO: do we need to sanitize ptype?  Can it bust out of the string and do '"; drop TABLES;' or whatever?
		return sysUserRepo.findProperties(su.get(), ptype);
	}
	
	public Optional<SystemUserProperty> getProperty(String ptype, String pname) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		Assert.doesNotContain(ptype, "*","Can't get property with wildcard type");
		Assert.doesNotContain(ptype, "%","Can't get property with wildcard type");
		return sysUserRepo.getProperty(su.get(), ptype, pname);
	}
	
	public Optional<SystemUserProperty> deleteProperty(String ptype, String pname) {
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
	
	public int createProperty(String ptype, String pname, String pvalue) {
		Optional<SystemUser> su = getSystemUser();
		Assert.isTrue(su.isPresent(), "Invalid user");
		return sysUserRepo.createProperty(ptype, pname, pvalue, su.get().getId());
	}

	
	
}
