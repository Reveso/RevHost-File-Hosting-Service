package com.lukasrosz.revhost.storage.service;

import java.io.InputStream;
import java.util.List;

import com.lukasrosz.revhost.storage.entity.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import com.lukasrosz.revhost.exception.AccessToFileDeniedException;

public interface StorageService {
	
	boolean store(MultipartFile file) throws Exception;
	
	List<FileDTO> loadLoggedUserFiles();
	
	FileDTO loadFile(String code) throws AccessToFileDeniedException;
	
	InputStream loadAsInputStream(String code) throws AccessToFileDeniedException;

	void deleteFile(String fileCode) throws AccessToFileDeniedException;
	
	void deleteAll(String username) throws AccessToFileDeniedException;

	void setFileAccess(String fileCode, String access) throws AccessToFileDeniedException;
}
