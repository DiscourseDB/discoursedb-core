package edu.cmu.cs.lti.discoursedb.core.repository;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * The CoreBaseTypeRepository extends the CoreBaseRepository and contains common
 * access methods for types
 * 
 * @author Oliver Ferschke
 *
 * @param <T>
 *            the entity type
 * @param <ID>
 *            the primary key type (usually long)
 */
@NoRepositoryBean
public interface CoreBaseTypeRepository<T, ID extends Serializable> extends CoreBaseRepository<T, ID>, QueryDslPredicateExecutor<T> {

	Optional<T> findOneByType(String type);

}
