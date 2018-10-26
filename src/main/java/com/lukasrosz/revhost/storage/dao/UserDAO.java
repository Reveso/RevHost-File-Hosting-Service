package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import com.lukasrosz.revhost.storage.entity.UserDTO;

public interface UserDAO {
	
	public List<UserDTO> getUsersList();

	public UserDTO getUser(String username);

	public void saveUser(UserDTO userDTO);

	void deleteUser(String username);

}
