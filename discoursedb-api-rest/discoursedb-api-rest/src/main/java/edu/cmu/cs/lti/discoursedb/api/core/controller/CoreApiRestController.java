package edu.cmu.cs.lti.discoursedb.api.core.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.api.core.resource.macro.DiscourseResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationContributionResource;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

@Controller
@RequestMapping( value = "/api", produces = "application/hal+json" )
public class CoreApiRestController {

	@Autowired
	private DiscourseRepository discourseRepository;
	@Autowired
	UserRepository userRepository;
	
    @RequestMapping(method=RequestMethod.GET)
    @ResponseBody 
    Resources<DiscourseResource> discourses() {
		List<DiscourseResource> resourceList = StreamSupport
				.stream(discourseRepository.findAll().spliterator(), true)
				.map(DiscourseResource::new)
				.collect(Collectors.toList());
        return new Resources<DiscourseResource>(resourceList);
    }

		
	  @RequestMapping(value="/user/{id}",method=RequestMethod.GET)
	  public User user(@PathVariable Long id) {				  
		  return userRepository.findById(id).get();
	  }


}
