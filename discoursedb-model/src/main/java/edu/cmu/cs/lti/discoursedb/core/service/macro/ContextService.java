package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Context;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContextRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionContextRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContextTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class ContextService {

	private final @NonNull ContextRepository contextRepo;
	private final @NonNull ContributionContextRepository contributionContextRepo;
	
	/**s
	 * Retrieves existing or creates a new ContextType entity with the
	 * provided type. It then creates a new empty Context entity and
	 * connects it with the type. Both changed/created entities are saved to
	 * DiscourseDB and the empty typed Context is returned. It then adds
	 * the new empty Context to the db and returns the object.
	 * 
	 * @param type
	 *            the value for the ContextType
	 * @return a new empty Context that is already saved to the db and
	 *         connected with its requested type
	 */
	public Context createTypedContext(ContextTypes type){
		Assert.notNull(type, "Context type cannot be null.s");
		Context context = new Context();
		context.setType(type.name());
		return contextRepo.save(context);
	}		
	
	/**
	 * Adds the given contribution to the provided Context.
	 * The start date of the relation between the two is initialized with the creation date of the contribution.
	 * 
	 * @param context the Context for the given contribution
	 * @param contrib the contribution that is part of the given DiscoursePart.
	 */
	public void addContributionToContext(Context context, Contribution contrib){	
		Assert.notNull(context, "Context cannot be null.");
		Assert.notNull(contrib, "Contribution to add to Context cannot be null.");
		Optional<ContributionContext> existingContributionContext = contributionContextRepo.findOneByContributionAndContext(contrib, context);
		if(!existingContributionContext.isPresent()){
			ContributionContext contributionContext = new ContributionContext();
			contributionContext.setContribution(contrib);
			contributionContext.setContext(context);
			contributionContext.setStartTime(contrib.getStartTime());	
			contributionContextRepo.save(contributionContext);
			contrib.addContributionContexts(contributionContext);
			context.addContextContributions(contributionContext);
		}	
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Context findOne(Long id){
		Assert.notNull(id, "Context id cannot be null.");
		Assert.isTrue(id>0, "Context id has to be a positive number.");
		return contextRepo.findOne(id);
	}
	public Context save(Context ctx){
		Assert.notNull(ctx, "Context cannot be null.");
		return contextRepo.save(ctx);
	}
	

}