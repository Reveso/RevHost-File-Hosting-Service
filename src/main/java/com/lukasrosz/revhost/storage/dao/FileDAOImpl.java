package com.lukasrosz.revhost.storage.dao;

import java.util.List;

import org.hibernate.query.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.lukasrosz.revhost.storage.entities.RevHostFile;

@Repository
public class FileDAOImpl implements FileDAO {

	@Autowired
	private SessionFactory revhostSessionFactory;
	
	
	
	@Override
	public List<String> getAllFileCodes() {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<String> codessQuery = session.createQuery("SELECT code FROM RevHostFile", String.class);
		
		List<String> codes = codessQuery.getResultList();
		return codes;
	}

	@Override
	public List<RevHostFile> getUserFiles(String username) {

		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<RevHostFile> filesQuery = session.createQuery("FROM RevHostFile WHERE username=:uname", RevHostFile.class);
		filesQuery.setParameter("uname", username);
		
		List<RevHostFile> files = filesQuery.getResultList();
		
		return files;
	}
	
	@Override
	public List<String> getUserFileCodes(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<String> codessQuery = session.createQuery("SELECT code FROM RevHostFile WHERE username=:uname", String.class);
		codessQuery.setParameter("uname", username);
		
		List<String> codes = codessQuery.getResultList();
		
		return codes;
	}

	@Override
	public void saveFile(RevHostFile file) {
		Session session = revhostSessionFactory.getCurrentSession();
		System.out.println(file);

		session.saveOrUpdate(file);
		session.flush();
	}

	@Override
	public RevHostFile getFile(String fileCode) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		RevHostFile file = session.get(RevHostFile.class, fileCode);
		return file;
	}

	@Override
	public void deleteFile(String fileCode) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<?> query = session.createQuery("DELETE FROM RevHostFile WHERE code=:fileCode");
		query.setParameter("fileCode", fileCode);
		
		query.executeUpdate();
	}
	
	@Override
	public void deleteAllFilesOfUser(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<?> query = session.createQuery("DELETE FROM RevHostFile WHERE username=:uname");
		query.setParameter("uname", username);
		
		query.executeUpdate();
	}
	
	@Override
	public List<RevHostFile> getUserPublicFiles(String username) {
		Session session = revhostSessionFactory.getCurrentSession();
		
		Query<RevHostFile> query = session.createQuery("FROM RevHostFile WHERE access=public", RevHostFile.class);
		List<RevHostFile> files = query.getResultList();
		
		return files;
	}
	

}
