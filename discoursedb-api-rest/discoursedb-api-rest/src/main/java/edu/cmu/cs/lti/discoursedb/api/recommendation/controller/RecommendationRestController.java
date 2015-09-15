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
@RequestMapping(value = "/recommendation", produces = "application/hal+json")
public class RecommendationRestController {

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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationDiscourseResource> discourses() {
		List<RecommendationDiscourseResource> discourseResources = StreamSupport
				.stream(discourseRepository.findAll().spliterator(), false)
				.map(RecommendationDiscourseResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDiscourseResource>(discourseResources);
	}

	@RequestMapping(value = "/allcontributions", method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationContributionResource> contributions() {
		List<RecommendationContributionResource> contribResources = StreamSupport
				.stream(contributionRepository.findAll().spliterator(), false)
				.map(RecommendationContributionResource::new).collect(Collectors.toList());
		return new Resources<RecommendationContributionResource>(contribResources);
	}

	@RequestMapping(value = "/allusers", method = RequestMethod.GET)
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
		return new RecommendationContributionResource(getThreadStarter(id));				
	}
	
	public Contribution getThreadStarter(String contribSourceId){
		//TODO check if optional is present
		Contribution contrib= contributionRepository.findOneBySourceId(contribSourceId).get();
		Contribution parent=null;
		for(DiscourseRelation rel:contrib.getTargetOfDiscourseRelations()){
			if(rel.getType().getType().equals(DiscourseRelationTypes.DESCENDANT.name())){
				parent=rel.getSource();
			}	
		}
		return parent==null?contrib:parent;
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

	@RequestMapping(value = "/discoursePartsOfDiscourse/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationDiscoursePartResource> discourseToDiscourseParts(@PathVariable Long id) {
		Discourse discourse = discourseRepository.findOne(id);
		List<RecommendationDiscoursePartResource> discoursePartResources = discourseToDiscoursePartRepository.findByDiscourse(discourse).stream().map(e -> e.getDiscoursePart()).map(RecommendationDiscoursePartResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDiscoursePartResource>(discoursePartResources);
	}

	@RequestMapping(value = "/contributionsOfDiscoursePart/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationContributionResource> contributionsForDiscoursePart(@PathVariable Long id) {
		DiscoursePart discoursePart = discoursePartRepository.findOne(id);		
		List<RecommendationContributionResource> discoursePartResources = discoursePartContributionRepository.findByDiscoursePart(discoursePart).stream().map(e -> e.getContribution()).map(RecommendationContributionResource::new).collect(Collectors.toList());
		return new Resources<RecommendationContributionResource>(discoursePartResources);
	}

	@RequestMapping(value = "/usersOfDiscoursePart/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationUserResource> usersForDiscoursePart(@PathVariable Long id) {
		DiscoursePart discoursePart = discoursePartRepository.findOne(id);		
		List<RecommendationUserResource> discoursePartResources = discoursePartContributionRepository.findByDiscoursePart(discoursePart).stream().map(e -> e.getContribution().getCurrentRevision().getAuthor()).map(RecommendationUserResource::new).collect(Collectors.toList());
		return new Resources<RecommendationUserResource>(discoursePartResources);
	}

}
