package com.lukasrosz.revhost.storage.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lukasrosz.revhost.storage.entities.RevHostFile;

public interface StorageService {
	
	boolean store(MultipartFile file);
	
	List<RevHostFile> loadAll(String username);
	
	RevHostFile loadFile(String code);
	
	InputStream loadAsInputStream(String code);

	boolean deleteFile(String fileCode);
	
	boolean deleteAll(String username);

	String getVideoUrl(String fileCode);
	
}
