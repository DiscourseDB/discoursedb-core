package edu.cmu.cs.lti.discoursedb.core.repository;

import java.io.Serializable;

import org.springframework.data.repository.NoRepositoryBean;



/**
 * The CoreBaseRepository interface defines the data access methods that every of the DiscourseDB Core repositories should have.
 * It extends DiscourseDB-forks of the Spring-Data repositories PagingAndSortingRepository, CrudRepository and QueryDslPredicateExecutor.
 * 
 * @author Oliver Ferschke
 *
 * @param <T> the entity type
 * @param <ID> the primary key type (usually long)
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID>, QueryDslPredicateExecutor<T> {
	
	
}
