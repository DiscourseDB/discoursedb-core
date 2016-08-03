package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;

@Projection(types = { DiscourseToDiscoursePart.class }, name = "d2dp")
public
interface BrowsingD2DP  {
	
	//DiscoursePart getDiscoursePart();
	
	@Value("#{target.discoursePart.getName()}")
	int getName();
	@Value("#{target.discoursePart.getType()}")
	int getType();
	
}
