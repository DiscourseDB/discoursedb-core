/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscourse;
import edu.cmu.cs.lti.discoursedb.core.model.system.Dataset;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class DiscourseService {

	private final @NonNull DiscourseRepository discourseRepository;
	private final @NonNull DataSourceService dataSourceService;

	/**
	 * Returns a Discourse object with the given name if it exists or creates a
	 * new Discourse entity with that name, stores it in the database and
	 * returns the entity object.
	 * 
	 * @param name
	 *            name of the requested discourse
	 * @return the Discourse object with the given name - either retrieved or
	 *         newly created
	 */
	public Discourse createOrGetDiscourse(String name, Dataset dataset) {
		Assert.hasText(name, "Discourse name cannot be empty");
		return discourseRepository.findOneByName(name).orElseGet(()->{
			Discourse disc = new Discourse(name);
			disc.setDatasetId(dataset.getDatasetId());
			return discourseRepository.save(disc);});
	}
	
	/**
	 * Returns a Discourse object with the given name if it exists or creates a
	 * new Discourse entity with that name, stores it in the database and
	 * returns the entity object.
	 * 
	 * @param name
	 *            name of the requested discourse
	 * @return the Discourse object with the given name - either retrieved or
	 *         newly created
	 */
	public Discourse createOrGetDiscourse(String name, String datasetName) {
		Assert.hasText(name, "Discourse name cannot be empty");
		return discourseRepository.findOneByName(name).orElseGet(()->{
			Discourse disc = new Discourse(name);
			Dataset dataset = dataSourceService.createOrGetDataset(datasetName);
			disc.setDatasetId(dataset.getDatasetId());
			return discourseRepository.save(disc);});
	}
	
	/**
	 * Returns a Discourse object with the given name if it exists 
	 * 
	 * @param name name of the requested discourse
	 * @return an Optional containing the Discourse object with the given name if it exists 
	 */
	public Optional<Discourse> findOne(String name) {
		Assert.hasText(name, "Discourse name cannot be empty");
		return discourseRepository.findOneByName(name);
	}

	/**
	 * Finds one DiscoursePart of the given type, with the given name and associated with the given discourse
	 *  
	 * @param discoursePart the DiscoursePart for which the discourse should be retrieved
	 * @return and Optional that contains a Discourse if it exists
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<Discourse> findOne(DiscoursePart discoursePart){
		Assert.notNull(discoursePart, "DiscoursePart cannot be null.");
		
		return Optional.ofNullable(discourseRepository
				.findOne(QDiscourse.discourse.discourseToDiscourseParts.any().discoursePart.eq(discoursePart)));
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<Discourse> findOne(Long id){
		Assert.notNull(id, "Id cannot be null.");
		Assert.isTrue(id>0, "Id must be a positive number.");

		return discourseRepository.findOne(id);
	}
	
	/**
	 * Calls the save method of the Discourse repository to save the given
	 * Discourse object to the DB. Returns the Discourse object after the save
	 * process.
	 * 
	 * @param discourse
	 *            the Discourse object to save
	 * @return the (potentially altered) Discourse object that is returned after
	 *         the save process
	 */
	public Discourse save(Discourse discourse) {
		Assert.notNull(discourse, "Discourse cannot be null.");

		return discourseRepository.save(discourse);
	}
	
	public Iterable<Discourse> findAll(){
		return discourseRepository.findAll();
	}
	
	

}
