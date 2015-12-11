package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Context;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContextType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContextRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContextTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionContextRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContextTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContextService {

	@Autowired
	private ContextRepository contextRepo;
	@Autowired
	private ContextTypeRepository contextTypeRepo;
	@Autowired
	private ContributionContextRepository contributionContextRepo;
	
	/**
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
		Assert.notNull(type);
		Optional<ContextType> existingContextType = contextTypeRepo.findOneByType(type.name());
		ContextType contextType = null;
		if(existingContextType.isPresent()){
			contextType = existingContextType.get();
		}else{
			contextType = new ContextType();
			contextType.setType(type.name());
			contextType= contextTypeRepo.save(contextType);
		}

		Context context = new Context();
		context.setType(contextType);
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
		Assert.notNull(context);
		Assert.notNull(contrib);
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
	public Context findOne(long id){
		Assert.isTrue(id>0);
		return contextRepo.findOne(id);
	}
	public Context save(Context ctx){
		Assert.notNull(ctx);
		return contextRepo.save(ctx);
	}
	

}