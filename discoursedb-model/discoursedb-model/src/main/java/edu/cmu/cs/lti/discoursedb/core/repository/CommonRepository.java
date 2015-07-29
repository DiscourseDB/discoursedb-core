package edu.cmu.cs.lti.discoursedb.core.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    String custom();
}
