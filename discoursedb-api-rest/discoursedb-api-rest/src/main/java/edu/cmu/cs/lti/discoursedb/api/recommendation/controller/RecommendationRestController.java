package edu.cmu.cs.lti.discoursedb.api.recommendation.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationContributionResource;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;

@Controller
@RequestMapping( value = "/recommendation", produces = "application/hal+json" )
public class RecommendationRestController {

    @Autowired
    private ContributionRepository contributionRepository;

    @Autowired
    private ContributionRepository userRepository;

    //TODO ACCESS to DiscourseRepo which gives access to the contrib and user lists of each discourse
    
    
    @RequestMapping(value = "/contributions", method=RequestMethod.GET)
    @ResponseBody 
    Resources<RecommendationContributionResource> contributions() {
		List<RecommendationContributionResource> recommendationResourceList = StreamSupport
				.stream(contributionRepository.findAll().spliterator(), true)
				.map(RecommendationContributionResource::new)
				.collect(Collectors.toList());
        return new Resources<RecommendationContributionResource>(recommendationResourceList);
    }

//    @RequestMapping(value = "/authors", method=RequestMethod.GET)
//    @ResponseBody 
//    Resources<RecommendationContributionResource> authors() {
//		List<RecommendationContributionResource> recommendationResourceList = StreamSupport
//				.stream(contributionRepository.findAll().spliterator(), true)
//				.map(RecommendationContributionResource::new)
//				.collect(Collectors.toList());
//        return new Resources<RecommendationContributionResource>(recommendationResourceList);
//    }

}
