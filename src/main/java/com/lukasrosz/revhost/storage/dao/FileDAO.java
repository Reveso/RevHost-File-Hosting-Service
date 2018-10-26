package com.lukasrosz.revhost.storage.dao;

import com.lukasrosz.revhost.storage.entity.FileDTO;

import java.util.List;

public interface FileDAO {

	List<FileDTO> getUserFiles(String username);
	
	List<String> getUserFileCodes(String username);
	
	void saveFile(FileDTO file);
	
	FileDTO getFile(String fileCode);
	
	void deleteFile(String fileCode);

	void deleteAllUserFiles(String username);

	List<String> getAllFileCodes();

	List<FileDTO> getUserPublicFiles(String username);
}
