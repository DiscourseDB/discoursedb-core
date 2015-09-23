package edu.cmu.cs.lti.discoursedb.sandbox;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;

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
	private UserService userService;

	@Autowired
	private DiscourseService discourseService;

	public static void main(String[] args) {
		SpringApplication.run(TestSpringDataJPA.class);
	}

	@Override
	public void run(String... strings) throws Exception {
		Discourse d = discourseService.createOrGetDiscourse("UTArlingtonX/LINK5.10x/3T2014");
		User u = userService.createOrGetUserByUsername(d, "ferschke");
		u.setRealname("Oliver Ferschke");
	}

}
