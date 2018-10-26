package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lukasrosz.revhost.storage.entity.UserDTO;

@Repository
public class UserDAOImpl implements UserDAO {

	@Autowired
	private SessionFactory revhostSessionFactory;

	@Override
	public List<UserDTO> getUsersList() {
		
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<UserDTO> usersQuery = session.createQuery("SELECT u FROM UserDTO u",
																UserDTO.class);
		
		List<UserDTO> usersList = usersQuery.getResultList();
		
		usersList.forEach(userDTO -> System.out.println(userDTO.getAuthorities()));
		return usersList;
	}

	@Override
	public UserDTO getUser(String username) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		UserDTO userDTO = session.get(UserDTO.class, username);
		
		return userDTO;
	}

	@Override
	public void saveUser(UserDTO userDTO) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		session.saveOrUpdate(userDTO);
	}
	
	@Override
	public void deleteUser(String username) {
		Session session = revhostSessionFactory.getCurrentSession();

		Query<?> query = session.createQuery("delete from UserDTO where username=:uname");
		query.setParameter("uname", username);
		
		query.executeUpdate();
	}

}
