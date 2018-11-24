package com.lukasrosz.revhost.storage.dao;

import com.lukasrosz.revhost.storage.model.FileDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FileDAO extends CrudRepository<FileDTO, String> {

	List<FileDTO> findByUsername(String username);

	@Query("select f.code from FileDTO f where f.username=username")
	List<String> findCodesByUsername(String username);

	@Query("select f.code from FileDTO f")
	List<String> findAllCodes();
}
