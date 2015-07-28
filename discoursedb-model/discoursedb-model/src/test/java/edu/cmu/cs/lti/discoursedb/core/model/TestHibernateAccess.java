package edu.cmu.cs.lti.discoursedb.core.model;

import java.util.List;

import org.hibernate.Session;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.util.HibernateUtil;

public class TestHibernateAccess {

	public static void main(String[] args) {		
		List<User> users = getAllUsers();
		System.out.println(users.size());
	}

	public static List<User> getAllUsers() {
        Session session = HibernateUtil.createSessionFactory().openSession();
        session.beginTransaction();
 
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) session.createQuery(
                "FROM User u ORDER BY u.realname ASC").list();
 
        session.getTransaction().commit();
        session.close();
        return users;
	}
}
