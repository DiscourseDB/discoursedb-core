package edu.cmu.cs.lti.discoursedb.configuration;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.cmu.cs.lti.discoursedb.core.configuration.PublicDummyUser;

public class Utilities {
	public static CoreAndSystemUser getCurrentUser() {
		try {
			return (CoreAndSystemUser) SecurityContextHolder.getContext().	
					getAuthentication().getPrincipal();
		} catch (Exception e) {
			System.out.println("ERROR: " + e);
			return null;
		}
	}
	public static void setCurrentUser(Authentication auth) {
		SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(auth);
	}
	public static void clearCurrentUser() {
		SecurityContextHolder.clearContext();
	}
	public static void becomeSuperUser() {
		SecurityContextHolder.getContext().	
				setAuthentication(new UsernamePasswordAuthenticationToken(
						new PublicDummyUser(),"dummy"));
	
	}
}
