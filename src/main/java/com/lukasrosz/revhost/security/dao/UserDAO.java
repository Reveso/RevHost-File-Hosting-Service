package com.lukasrosz.revhost.security.dao;

import com.lukasrosz.revhost.security.model.UserDTO;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<UserDTO, String> {

}
