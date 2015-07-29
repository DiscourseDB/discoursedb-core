package edu.cmu.cs.lti.discoursedb.core.model;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import edu.cmu.cs.lti.discoursedb.configuration.DiscourseDBConfig;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;

public class TestSpringHibernate {

	public static void main(String[] args) {
	      AbstractApplicationContext context = new AnnotationConfigApplicationContext(DiscourseDBConfig.class);
	      
	      UserService service = (UserService) context.getBean("userService");
	      
//	      User u1 = new User();
//	      u1.setRealname("Oliver Ferschke");
//	      u1.setUsername("ferschke");
//	      u1.setEmail("ferschke@cs.cmu.edu");
//	      service.saveOrUpdate(u1);

	      User uexisting = service.get(1);
	      uexisting.setUsername("olifer");
	      service.saveOrUpdate(uexisting);
	      
	      System.out.println(service.list().size()+" Users");
	      
	      context.close();
	}

}
