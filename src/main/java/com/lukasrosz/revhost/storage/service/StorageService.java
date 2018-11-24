package com.lukasrosz.revhost.storage.service;

import java.io.InputStream;
import java.util.List;

import com.lukasrosz.revhost.storage.model.FileDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	
	boolean store(MultipartFile file) throws Exception;
	
	List<FileDTO> loadLoggedUserFiles();
	
	FileDTO loadFile(String code) throws AccessDeniedException;
	
	InputStream loadAsInputStream(String code) throws AccessDeniedException;

	void deleteFile(String fileCode) throws AccessDeniedException;
	
	void deleteAll(String username) throws AccessDeniedException;

	void setFileAccess(String fileCode, String access) throws AccessDeniedException;
}
