package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.dao.user.UserDAO;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDAO dao;
	
	public List<User> list() {
		return dao.list();
	}

	public User get(int id) {
		return dao.get(id);
	}

	public void saveOrUpdate(User user) {
		dao.saveOrUpdate(user);
	}

	public void delete(int id) {
		dao.delete(id);
	}

}
