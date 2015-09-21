package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartType;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class DiscoursePartService {

	@Autowired
	private DiscoursePartRepository discourePartRepo;
	
	@Autowired
	private DiscoursePartTypeRepository discourePartTypeRepo;

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
