package edu.cmu.cs.lti.discoursedb.api.recommendation.controller;

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

import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationContributionResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationUserResource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;

@Controller
@RequestMapping(value = "/recommendation", produces = "application/hal+json")
public class RecommendationRestController {

	@Autowired
	private ContributionRepository contributionRepository;

	@Autowired
	private UserRepository userRepository;

	// TODO ACCESS to DiscourseRepo which gives access to the contrib and user
	// lists of each discourse

	@RequestMapping(value = "/contributions", method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationContributionResource> contributions() {
		List<RecommendationContributionResource> contribResources = StreamSupport
				.stream(contributionRepository.findAll().spliterator(), false)
				.map(RecommendationContributionResource::new).collect(Collectors.toList());
		return new Resources<RecommendationContributionResource>(contribResources);
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationUserResource> users() {
		List<RecommendationUserResource> userResources = StreamSupport
				.stream(userRepository.findAll().spliterator(), false).map(RecommendationUserResource::new)
				.collect(Collectors.toList());
		return new Resources<RecommendationUserResource>(userResources);
	}

	@RequestMapping(value = "/contributionParent/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationContributionResource contribParent(@PathVariable String id) {
		//TODO check if optional is present
		Contribution contrib= contributionRepository.findOneBySourceId(id).get();
		Contribution parent=null;
		for(DiscourseRelation rel:contrib.getTargetOfDiscourseRelations()){
			if(rel.getType().getType().equals(DiscourseRelationTypes.DESCENDANT.name())){
				parent=rel.getSource();
			}	
		}
		return parent==null?new RecommendationContributionResource(contrib):new RecommendationContributionResource(parent);		
	}

	@RequestMapping(value = "/contribution/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationContributionResource contrib(@PathVariable String id) {
		Contribution contrib = contributionRepository.findOneBySourceId(id).get();
		return new RecommendationContributionResource(contrib);
	}

	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationUserResource user(@PathVariable Long id) {
		User user = userRepository.findOne(id);
		System.out.println(user.getUsername());
		return new RecommendationUserResource(user);
	}
}
