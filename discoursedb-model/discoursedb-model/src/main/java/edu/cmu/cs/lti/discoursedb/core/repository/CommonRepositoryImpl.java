package edu.cmu.cs.lti.discoursedb.core.repository;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class CommonRepositoryImpl extends SimpleJpaRepository implements CommonRepository {
    
	public <T> CommonRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    public <T> CommonRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
                                EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public String custom() {
        return "CustomValue";
    }
}
