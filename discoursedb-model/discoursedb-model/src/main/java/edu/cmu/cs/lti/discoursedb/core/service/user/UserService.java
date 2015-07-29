package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public interface UserService {
    public List<User> list();
    
    public User get(int id);
     
    public void saveOrUpdate(User user);
     
    public void delete(int id);
}
