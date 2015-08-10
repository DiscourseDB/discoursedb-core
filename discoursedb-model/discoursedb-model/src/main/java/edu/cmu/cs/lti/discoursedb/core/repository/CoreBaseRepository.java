package edu.cmu.cs.lti.discoursedb.core.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The CoreBaseRepository interface defines the data access methods that every of the DiscourseDB Core repositories should have.
 * It already extends CrudRepository which provides basic CRUD methods to each repository.
 * This interface does not necessarily have to define additional methods.  
 * 
 * @author Oliver Ferschke
 *
 * @param <T> the entity type
 * @param <ID> the primary key type (usually long)
 */
@NoRepositoryBean
public interface CoreBaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
	

	}
