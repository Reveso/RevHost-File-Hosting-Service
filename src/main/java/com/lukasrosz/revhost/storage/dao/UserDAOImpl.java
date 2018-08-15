package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lukasrosz.revhost.storage.entities.User;

@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	private SessionFactory revhostSessionFactory;

	@Override
	public List<User> getUsersList() {
		
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<User> usersQuery = session.createQuery("SELECT u FROM User u", 
																User.class);
		
		List<User> usersList = usersQuery.getResultList();
		
		usersList.forEach(user -> System.out.println(user.getAuthorities()));
		return usersList;
	}

	@Override
	public User getUser(String username) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		User user = session.get(User.class, username);
		
		return user;
	}

	@Override
	public void saveUser(User user) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		session.saveOrUpdate(user);
	}
	
	@Override
	public void deleteUser(String username) {
		Session session = revhostSessionFactory.getCurrentSession();

		Query<?> query = session.createQuery("delete from User where username=:uname");
		query.setParameter("uname", username);
		
		query.executeUpdate();
	}

}
