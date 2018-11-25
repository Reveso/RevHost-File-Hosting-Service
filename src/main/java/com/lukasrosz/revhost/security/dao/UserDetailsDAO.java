package com.lukasrosz.revhost.security.dao;

import com.lukasrosz.revhost.security.model.UserDetails;
import org.springframework.data.repository.CrudRepository;

public interface UserDetailsDAO extends CrudRepository<UserDetails, Long> {
}
