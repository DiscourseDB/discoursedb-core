package edu.cmu.cs.lti.discoursedb.core.model.user.projection;

import org.springframework.data.rest.core.config.Projection;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

@Projection(name = "condensed", types={User.class})
public interface BasicUser {
	public String getRealname();

	public String getUsername();

	public String getEmail();

	public String getIp();

	public String getLanguage();

	public String getCountry();

	public String getLocation();
}
