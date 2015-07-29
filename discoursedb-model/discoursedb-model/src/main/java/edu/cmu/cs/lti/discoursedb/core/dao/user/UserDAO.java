package edu.cmu.cs.lti.discoursedb.core.dao.user;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public interface UserDAO {

    public List<User> list();
    
    public User get(int id);
     
    public void saveOrUpdate(User user);
     
    public void delete(int id);
	
}
