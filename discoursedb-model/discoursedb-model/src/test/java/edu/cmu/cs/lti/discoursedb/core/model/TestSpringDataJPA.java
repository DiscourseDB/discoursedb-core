package edu.cmu.cs.lti.discoursedb.core.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

@Configuration
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
	      }
	}
	

}
