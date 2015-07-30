package edu.cmu.cs.lti.discoursedb.core.model;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

/**
 * @author oliverf
 * 
 * 1. We need a componentscan in order to discover the configuration
 * 2. We need to wrap everything in a transaction in order to allow lazy loading to work.
 * 		If we didn't do that, than we would have a single transaction for each interaction with a data repository.
 * 		However, the transaction does not extend to the proxy objects retrieved in lazy loading
 *
 */
@Transactional 
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class TestSpringDataJPA implements CommandLineRunner {

	@Autowired
	private UserRepository userRepo;
	  
	public static void main(String[] args) {
		SpringApplication.run(TestSpringDataJPA.class);
	}
	
    @Override
    public void run(String... strings) throws Exception {
		for(User u:userRepo.findAll()){
	    	  System.out.println(u.getRealname());
	    	  System.out.println(u.getContentInteractions().size());
	      }
	}
	

}
