package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationContributionResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDataSourceInstanceResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDiscourseResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationUserResource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;

@Controller
@RequestMapping(value = "/browsing", produces = "application/hal+json")
public class BrowsingRestController {

	@Autowired
	private DiscourseRepository discourseRepository;

	@Autowired
	private DiscoursePartRepository discoursePartRepository;

	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepository;

	@Autowired
	DiscoursePartContributionRepository discoursePartContributionRepository;
	
	@Autowired
	private ContributionRepository contributionRepository;

	@Autowired
	private UserRepository userRepository;

	
	@RequestMapping(value = "/repos", method = RequestMethod.GET)
	@ResponseBody
	Resources<BrowsingDiscoursePartResource> contributions(@RequestParam(value= "page", defaultValue = "1") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size) {
		PageRequest p = new PageRequest(page,size);
		List<BrowsingDiscoursePartResource> repoResources = StreamSupport
				.stream(discoursePartRepository.findAllNonDegenerateByType("GITHUB_REPO", p).spliterator(), false)
				.map(BrowsingDiscoursePartResource::new).collect(Collectors.toList());
		return new Resources<BrowsingDiscoursePartResource>(repoResources);
	}
    

}
