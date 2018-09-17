package edu.cmu.cs.lti.discoursedb.core.configuration;

import edu.cmu.cs.lti.discoursedb.configuration.CoreAndSystemUser;

public class PublicDummyUser implements CoreAndSystemUser {

	
	@Override
	public String getEmail() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return "(Dummy public user)";
	}

}
