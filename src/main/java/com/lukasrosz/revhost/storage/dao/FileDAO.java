package com.lukasrosz.revhost.storage.dao;

import com.lukasrosz.revhost.storage.entities.RevHostFile;
import java.util.List;

public interface FileDAO {

	List<RevHostFile> getUserFiles(String username);
	
	List<String> getUserFileCodes(String username);
	
	void saveFile(RevHostFile file);
	
	RevHostFile getFile(String fileCode);
	
	void deleteFile(String fileCode);

	void deleteAllFilesOfUser(String username);

	List<String> getAllFileCodes();

	List<RevHostFile> getUserPublicFiles(String username);
}
