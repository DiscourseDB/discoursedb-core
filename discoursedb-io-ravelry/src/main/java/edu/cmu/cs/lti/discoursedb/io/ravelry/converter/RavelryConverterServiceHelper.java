package edu.cmu.cs.lti.discoursedb.io.ravelry.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
/*
 * This class is tightly coupled to RavelryConverterService.  Conceptually they're one class, but
 * separated for a technical reason: they need to be separate beans so that the REQUIRES_NEW propagation
 * property of Transactional will be obeyed.  Services are proxied, but calls within a service do not go
 * through the proxy, so transactions can't be manipulated between within-service method calls.
 * 
 * We need this because addPostingGroup is called for every 300 or so postings, to avoid transactions getting too big.
 * A transaction needs to be restarted for each clump, which means we *have* to call a method in a different service class.
 */
public class RavelryConverterServiceHelper {
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	
	public List<Integer>  addPostingGroup(int topic, List<Document> docs, RavelryConverterService ravelryConverterService) {
		List<Integer> postings  = new ArrayList<Integer>();
		Discourse discourse = discourseService.createOrGetDiscourse(ravelryConverterService.discourseName, ravelryConverterService.dataSetName);
		DiscoursePart thread = discoursePartService.findOneByDataSource("topics#" + topic, "ravelry#topics", ravelryConverterService.dataSetName).get();
		for (Document d: docs) {
			
			int post_id = d.getInteger("id");
			postings.add(post_id);
	
			
			Optional<Contribution> o_post = contributionService.findOneByDataSource("postings#" + Integer.toString(post_id), "ravelry#postings", ravelryConverterService.dataSetName);
			Contribution post = null;
			if (!o_post.isPresent()) {
				post = contributionService.createTypedContribution(ContributionTypes.POST);
	
				DataSourceInstance contribSource = dataSourceService.createIfNotExists(
						new DataSourceInstance("postings#" + Integer.toString(post_id),"ravelry#postings",ravelryConverterService.dataSetName));
				contribSource.setSourceType(DataSourceTypes.RAVELRY);
				dataSourceService.addSource(post, contribSource);		
				post.setStartTime(ravelryConverterService.stringToDate(d.getString("created_at")));
				
				//AnnotationInstance url = annoService.createTypedAnnotation("twitter_user_info");
				//annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getFavouritesCount()), "favorites_count"));
	
				Content curContent = contentService.createContent();
				curContent.setText(d.getString("body_html"));
				
				Map<String,String> patterns = ravelryConverterService.findPatterns(d.getString("body_html"));
				for (String pattern: patterns.keySet()) {
					Content pattContent = ravelryConverterService.createOrAddPattern(discourse, pattern);
					ContributionContext cclink = contributionService.addContextToContribution(post, pattContent);
					cclink.setType("MENTION_OF_" + patterns.get(pattern).toString().toUpperCase()); 
				}
				
				//curContent.setAuthor(user);
				curContent.setStartTime(ravelryConverterService.stringToDate(d.getString("created_at")));
				post.setCurrentRevision(curContent);
				post.setFirstRevision(curContent);		
			
				discoursePartService.addContributionToDiscoursePart(post, thread);						
	
				String username = ((org.bson.Document)d.get("user")).getString("username");
				User user = userService.createOrGetUser(discourse, username, "people#" + username,"ravelry#people", 
						DataSourceTypes.RAVELRY, ravelryConverterService.dataSetName);		
				curContent.setAuthor(user);
			}
		}
		return postings;
	}

}
