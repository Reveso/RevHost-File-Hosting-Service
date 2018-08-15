package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import com.lukasrosz.revhost.storage.entities.User;

public interface UserDAO {
	
	public List<User> getUsersList();

	public User getUser(String username);

	public void saveUser(User user);

	void deleteUser(String username);

}
