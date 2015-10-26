package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * This Service class maps blog entries to DiscourseDB
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class BlogConverterService {
	
	
}
