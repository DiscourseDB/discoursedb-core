package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class DiscoursePartService {

	@Autowired
	private DiscoursePartRepository discoursePartRepo;
	
	@Autowired
	private DiscoursePartTypeRepository discourePartTypeRepo;

	@Autowired
	private DiscoursePartContributionRepository discoursePartContributionRepo;

	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepo;
	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new empty DiscoursePart entity,
	 * connects it with the type and the provided discourse.<br/>
	 * 
	 * All changed/created entities are committed to the db and the DiscoursePart is returned.<br/>
	 * 
	 * The discoursePartName is constructed like this: <code>discourseName_DiscoursePartType</code>.<br/>
	 * Use {@link #createOrGetTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type)} to explicitly set the discoursePartName. 
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createOrGetTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type){
		return createOrGetTypedDiscoursePart(discourse,discourse.getName()+"_"+type.name(),type);
	}
	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new empty DiscoursePart entity,
	 * connects it with the type and the provided discourse.
	 * 
	 * All changed/created entities are committed to the db and the DiscoursePart is returned.
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param discoursePartName
	 *            the name of the discoursePart that should be retrieved or created
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createOrGetTypedDiscoursePart(Discourse discourse, String discoursePartName, DiscoursePartTypes type){		
		Optional<DiscoursePartType> optDiscoursePartType = discourePartTypeRepo.findOneByType(type.name());
		DiscoursePartType discoursePartType = null;
		if(optDiscoursePartType.isPresent()){
			discoursePartType = optDiscoursePartType.get();
		}else{
			discoursePartType = new DiscoursePartType();
			discoursePartType.setType(type.name());
			discoursePartType= discourePartTypeRepo.save(discoursePartType);
		}		
		
		//check if this exact discoursePart already exists, reuse it if it does and create it if it doesn't
		Optional<DiscoursePart> existingDiscoursePart = Optional.ofNullable(discoursePartRepo.findOne(
						DiscoursePartPredicates.discoursePartHasName(discoursePartName).and(
						DiscoursePartPredicates.discoursePartHasType(discoursePartType).and(
						DiscoursePartPredicates.discoursePartHasDiscourse(discourse)))));

		DiscoursePart dPart;
		if(existingDiscoursePart.isPresent()){
			dPart=existingDiscoursePart.get();
		}else{
			dPart=new DiscoursePart();
			dPart.setType(discoursePartType);
			dPart.setName(discoursePartName);
			dPart = discoursePartRepo.save(dPart);
		}
		
		Optional<DiscourseToDiscoursePart> existingDiscourseToDiscoursePart = discourseToDiscoursePartRepo.findOneByDiscourseAndDiscoursePart(discourse, dPart);	
		if(!existingDiscourseToDiscoursePart.isPresent()){
			DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();			
			discourseToDiscoursePart.setDiscourse(discourse);
			discourseToDiscoursePart.setDiscoursePart(dPart);
			discourseToDiscoursePartRepo.save(discourseToDiscoursePart);			
		}
		
		return dPart;
	}		

		
	/**
	 * Adds the given contribution to the provided DiscoursePart.
	 * The start date of the relation between the two is initialized with the creation date of the contribution.
	 * 
	 * In case this is not true, the DiscoursePartContribution relation has to be created manually or updated accordingly. 
	 * 
	 * @param contrib the contribution that is part of the given DiscoursePart.
	 * @param dPArt the DiscoursePart that contains the given contribution.
	 */
	public void addContributionToDiscoursePart(Contribution contrib, DiscoursePart dPArt){	
		Optional<DiscoursePartContribution> existingDiscoursePartContrib = discoursePartContributionRepo.findOneByContributionAndDiscoursePart(contrib, dPArt);
		if(!existingDiscoursePartContrib.isPresent()){
			DiscoursePartContribution discoursePartContrib = new DiscoursePartContribution();
			discoursePartContrib.setContribution(contrib);
			discoursePartContrib.setDiscoursePart(dPArt);
			discoursePartContrib.setStartTime(contrib.getStartTime());	
			discoursePartContributionRepo.save(discoursePartContrib);
			dPArt.addDiscoursePartContribution(discoursePartContrib);
			contrib.addContributionPartOfDiscourseParts(discoursePartContrib);
		}	
	}
	
	/**
	 * Saves the provided entity to the db using the save method of the corresponding repository
	 * 
	 * @param part the entity to save
	 * @return the possibly altered entity after the save process 
	 */
	public DiscoursePart save(DiscoursePart part){
		return discoursePartRepo.save(part);
	}
	
	public Optional<DiscoursePart> findOneByName(String name){
		return discoursePartRepo.findOneByName(name);		
	}

}
