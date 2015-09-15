package edu.cmu.cs.lti.discoursedb.sandbox;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

/**
 * 1. We need a ComponentScan in order to discover the configuration
 * 
 * 2. We need to wrap everything in a transaction in order to allow lazy loading
 * to work. If we didn't do that, than we would have a single transaction for
 * each interaction with a data repository. However, the transaction does not
 * extend to the proxy objects retrieved in lazy loading
 * 
 * @author Oliver Ferschke
 *
 */
@Transactional
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class TestSpringDataJPA implements CommandLineRunner {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContributionRepository contribRepo;
	
	@Autowired DiscourseRelationRepository discRelRepo;

	public static void main(String[] args) {
		SpringApplication.run(TestSpringDataJPA.class);
	}

	@Override
	public void run(String... strings) throws Exception {
//		Discourse discourse = new Discourse("Test","Phase6");
	//	discourseRepo.save(discourse);
				
		User user = new User();
		user.setRealname("First");
		user.setUsername("doe");
		user.setSourceId("123");
//		user.addDiscourses(discourse);	
		userRepo.save(user);	

		User user2 = new User();
		user2.setRealname("Second");
		user2.setUsername("doe");
		user2.setSourceId("456");
//		user2.addDiscourses(discourse);	
		userRepo.save(user2);	

		User retrievedUser = userRepo.findBySourceIdAndUsername("456", "doe").get();
		System.out.println(retrievedUser.getRealname());
		
		
		
//		Contribution c1 = new Contribution();
//		Contribution c2 = new Contribution();
//		DiscourseRelation rel = new DiscourseRelation();
//		rel.setSource(c1);
//		rel.setTarget(c2);
//		DiscourseRelationType type = new DiscourseRelationType();
//		type.setType("TEST");
//		rel.setType(type);
//		discRelRepo.save(rel);
//		contribRepo.save(c1);
//		contribRepo.save(c2);
		
		
//		for (User u : userRepo.findAll()) {
//			System.out.println(u.getUsername());
//			System.out.println(u.getDiscourses().size());
//		}
	}

}
