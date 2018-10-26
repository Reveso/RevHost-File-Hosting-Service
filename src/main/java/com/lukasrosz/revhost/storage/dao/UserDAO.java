package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import com.lukasrosz.revhost.storage.entity.UserDTO;
import org.springframework.data.repository.CrudRepository;

public interface UserDAO extends CrudRepository<UserDTO, String> {

}
