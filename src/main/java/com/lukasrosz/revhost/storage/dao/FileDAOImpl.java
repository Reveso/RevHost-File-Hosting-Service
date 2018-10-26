package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import com.lukasrosz.revhost.storage.entity.FileDTO;
import org.hibernate.query.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileDAOImpl implements FileDAO {

	@Autowired
	private SessionFactory revhostSessionFactory;
	
	@Override
	public List<String> getAllFileCodes() {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<String> codesQuery = session.createQuery("SELECT code FROM FileDTO", String.class);
		
		List<String> codes = codesQuery.getResultList();
		return codes;
	}

	@Override
	public List<FileDTO> getUserFiles(String username) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<FileDTO> filesQuery = session.createQuery("FROM FileDTO WHERE username=:uname", FileDTO.class);
		filesQuery.setParameter("uname", username);
		
		List<FileDTO> files = filesQuery.getResultList();
		
		return files;
	}
	
	@Override
	public List<String> getUserFileCodes(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<String> codesQuery = session.createQuery("SELECT code FROM FileDTO WHERE username=:uname", String.class);
		codesQuery.setParameter("uname", username);
		
		List<String> codes = codesQuery.getResultList();
		
		return codes;
	}

	@Override
	public void saveFile(FileDTO file) {
		Session session = revhostSessionFactory.getCurrentSession();
		System.out.println(file);

		session.saveOrUpdate(file);
		session.flush();
	}

	@Override
	public FileDTO getFile(String fileCode) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		FileDTO file = session.get(FileDTO.class, fileCode);
		return file;
	}

	@Override
	public void deleteFile(String fileCode) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<?> query = session.createQuery("DELETE FROM FileDTO WHERE code=:fileCode");
		query.setParameter("fileCode", fileCode);
		
		query.executeUpdate();
	}
	
	@Override
	public void deleteAllUserFiles(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<?> query = session.createQuery("DELETE FROM RevHostFile WHERE username=:uname");
		query.setParameter("uname", username);
		
		query.executeUpdate();
	}
	
	@Override
	public List<FileDTO> getUserPublicFiles(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<FileDTO> query = session.createQuery("FROM FileDTO WHERE access=public", FileDTO.class);
		List<FileDTO> files = query.getResultList();
		
		return files;
	}
}
