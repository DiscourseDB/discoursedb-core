package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.configuration.CoreAndSystemUser;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemDatabaseRepository;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemUserRepository;

public class SystemUserAuthentication extends UsernamePasswordAuthenticationToken  {
	
	public SystemUserAuthentication(Object principal, Object credentials) {
		super(principal, credentials);
	}
	
	public SystemUserAuthentication(SystemUser principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities, List<String> allowedDatabases) {
		super(principal, credentials, authorities);
		setAllowedDatabases(allowedDatabases);
	}
		
	

	// Just a reminder that the principal should be a SystemUser
	public SystemUser getSystemUser() { 
		return (SystemUser)this.getPrincipal(); 
	}
	
	public boolean canSeeDatabase(String database) {
		return authoritiesContains(database);
	}

	public void authorizedDatabaseCheck(String database) {
		if (!canSeeDatabase(database)) {
			throw new BrowsingRestController.UnauthorizedDatabaseAccess();
		}
	}
	public void authorizedDatabaseFail() {
		//logger.info("No database identified -- so authorization fails");
		throw new BrowsingRestController.UnauthorizedDatabaseAccess();
	}
	 
	public boolean hasRole(String roleName) {
		return authoritiesContains("ROLE:" + roleName);
	}
	 
	
	public List<String>allowedRoles() {
		List<String> allowed = new ArrayList<String>();
		for (GrantedAuthority ga : getAuthorities()) {
			String authority = ga.getAuthority();
			if (authority.startsWith("ROLE:")) {
				allowed.add(ga.getAuthority());
			}
		}
		return allowed;
	}
	
	
	public static boolean securityEnabled = false;
	
	
	public List<String> allowedDatabases = new ArrayList<String>();
	public void setAllowedDatabases(List<String> dbs) {
		allowedDatabases = dbs;
	}
	public List<String> getAllowedDatabases() {
		return allowedDatabases;
	}
	
	public static boolean authoritiesContains(String roledescription, Authentication who) {
		if (!securityEnabled) { return true; }  
		for (GrantedAuthority ga : who.getAuthorities()) {
			if (ga.getAuthority().equals(roledescription)) {
				return true;
			}
		}
		if (SystemUserAuthentication.class.isInstance(who)) {
			SystemUserAuthentication suawho = (SystemUserAuthentication)who;
			for (String db: suawho.getAllowedDatabases()) {
				if (roledescription.equals(db)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean authoritiesContains(String roledescription) {
		return authoritiesContains(roledescription, this);
	}

	
	
	
	
	
	
	
	
	
	
}
