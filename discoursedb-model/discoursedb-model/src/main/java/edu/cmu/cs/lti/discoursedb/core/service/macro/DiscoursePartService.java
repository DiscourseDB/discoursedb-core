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
	private DiscoursePartRepository discourePartRepo;
	
	@Autowired
	private DiscoursePartTypeRepository discourePartTypeRepo;

	@Autowired
	private DiscoursePartContributionRepository discoursePartContributionRepo;

	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepo;

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

		Optional<DiscoursePart> curOPtDiscoursePart = findOneByName(discoursePartName);
		DiscoursePart dPart = new DiscoursePart();
		if(curOPtDiscoursePart.isPresent()){
			dPart=curOPtDiscoursePart.get();
		}else{
			dPart.setType(discoursePartType);
			dPart = discourePartRepo.save(dPart);
		}
		
		Optional<DiscourseToDiscoursePart> optDiscourseToDiscoursePart = discourseToDiscoursePartRepo.findOneByDiscourseAndDiscoursePart(discourse, dPart);	
		if(!optDiscourseToDiscoursePart.isPresent()){
			DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();			
			discourseToDiscoursePart.setDiscourse(discourse);
			discourseToDiscoursePart.setDiscoursePart(dPart);
			discourseToDiscoursePartRepo.save(discourseToDiscoursePart);			
		}
		
		return dPart;
	}		


	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new DiscoursePart entity and
	 * connects it with the type and the provided discourse.
	 * 
	 * The changed/created entities are saved to
	 * DiscourseDB and the empty typed DiscoursePart is returned. It then adds
	 * the new empty DiscoursePart to the db. 
	 * 
	 * @param discourse
	 *            the discourse of which the new DiscoursePart is a part of
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createTypedDiscoursePart(Discourse discourse, DiscoursePartTypes type){		
		Optional<DiscoursePartType> optDiscoursePartType = discourePartTypeRepo.findOneByType(type.name());
		DiscoursePartType discoursePartType = null;
		if(optDiscoursePartType.isPresent()){
			discoursePartType = optDiscoursePartType.get();
		}else{
			discoursePartType = new DiscoursePartType();
			discoursePartType.setType(type.name());
			discoursePartType= discourePartTypeRepo.save(discoursePartType);
		}		

		DiscoursePart dPart = new DiscoursePart();
		dPart.setType(discoursePartType);
		dPart = discourePartRepo.save(dPart);
		
		DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();
		discourseToDiscoursePart.setDiscourse(discourse);
		discourseToDiscoursePart.setDiscoursePart(dPart);
		discourseToDiscoursePartRepo.save(discourseToDiscoursePart);
		
		return dPart;
	}		

	
	/**
	 * Retrieves existing or creates a new DiscoursePartType entity with the
	 * provided type. It then creates a new empty DiscoursePart entity and
	 * connects it with the type. Both changed/created entities are saved to
	 * DiscourseDB and the empty typed DiscoursePart is returned. It then adds
	 * the new empty discoursePart to the db and returns the object
	 * 
	 * @param type
	 *            the value for the DiscoursePartType
	 * @return a new empty DiscoursePart that is already saved to the db and
	 *         connected with its requested type
	 */
	public DiscoursePart createTypedDiscoursePart(DiscoursePartTypes type){		
		Optional<DiscoursePartType> optDiscoursePartType = discourePartTypeRepo.findOneByType(type.name());
		DiscoursePartType discoursePartType = null;
		if(optDiscoursePartType.isPresent()){
			discoursePartType = optDiscoursePartType.get();
		}else{
			discoursePartType = new DiscoursePartType();
			discoursePartType.setType(type.name());
			discoursePartType= discourePartTypeRepo.save(discoursePartType);
		}

		DiscoursePart dPart = new DiscoursePart();
		dPart.setType(discoursePartType);
		return discourePartRepo.save(dPart);
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
		Optional<DiscoursePartContribution> curOptDiscoursePartContrib = discoursePartContributionRepo.findOneByContributionAndDiscoursePart(contrib, dPArt);
		if(!curOptDiscoursePartContrib.isPresent()){
			DiscoursePartContribution discoursePartContrib = new DiscoursePartContribution();
			discoursePartContrib.setContribution(contrib);
			discoursePartContrib.setDiscoursePart(dPArt);
			discoursePartContrib.setStartTime(contrib.getStartTime());	
			dPArt.addDiscoursePartContribution(discoursePartContrib);
			contrib.addContributionPartOfDiscourseParts(discoursePartContrib);
			discoursePartContributionRepo.save(discoursePartContrib);
		}	
	}
	
	/**
	 * Saves the provided entity to the db using the save method of the corresponding repository
	 * 
	 * @param part the entity to save
	 * @return the possibly altered entity after the save process 
	 */
	public DiscoursePart save(DiscoursePart part){
		return discourePartRepo.save(part);
	}
	
	public Optional<DiscoursePart> findOneByName(String name){
		return discourePartRepo.findOneByName(name);		
	}

}
