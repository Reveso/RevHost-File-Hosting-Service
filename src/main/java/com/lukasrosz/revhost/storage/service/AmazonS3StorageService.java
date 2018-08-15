package com.lukasrosz.revhost.storage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.lukasrosz.revhost.storage.dao.FileDAO;
import com.lukasrosz.revhost.storage.dao.UserDAO;
import com.lukasrosz.revhost.storage.entities.RevHostFile;

@Service
public class AmazonS3StorageService implements StorageService {

	private AmazonS3 s3client;
	
	private UserDAO userDAO;
	
	private FileDAO fileDAO;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;

	@Value("${amazonProperties.bucketName}")
	private String bucketName;

	@Value("${amazonProperties.accessKey}")
	private String accessKey;

	@Value("${amazonProperties.secretKey}")
	private String secretKey;

	@Autowired
	public AmazonS3StorageService(UserDAO userDAO, FileDAO fileDAO) {
		this.fileDAO = fileDAO;
		this.userDAO = userDAO;
	}
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public boolean store(MultipartFile multipartFile) {
		String loggedUser = getLoggedUser();
		String fileCode = RevHostFile.generateNewCode(fileDAO.getAllFileCodes());
		String filePath = "";

		try {
			File file = convertMultipartToFile(multipartFile);
			filePath = generateFilePath(multipartFile, fileCode, loggedUser);

			uploadFileToS3Bucket(filePath, file);
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		RevHostFile dbFile = new RevHostFile();
		dbFile.setCode(fileCode);
		dbFile.setBucketName(bucketName);
		dbFile.setKey(filePath);
		dbFile.setName(multipartFile.getOriginalFilename().replace(" ", "_"));

		if (dbFile.getName().endsWith(".mp4")) {
			dbFile.setType("video");
		} else {
			dbFile.setType("file");
		}
		
		dbFile.setSize(multipartFile.getSize());
		dbFile.setAccess("private");
		dbFile.setUsername(getLoggedUser());
		
		dbFile.setAdditionDate(new Date()); //TODO set addition date

		System.out.println(dbFile);
		fileDAO.saveFile(dbFile);
		return true;
	}

	private void uploadFileToS3Bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.Private));
	}

	private File convertMultipartToFile(MultipartFile file) throws IOException {

		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fStream = new FileOutputStream(convFile);
		fStream.write(file.getBytes());
		fStream.close();

		return convFile;
	}

	private String getLoggedUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	private String generateFilePath(MultipartFile multiPart, String fileCode, String username) {
		return "storage" + "/" + username + "/" + fileCode + "/" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public List<RevHostFile> loadAll(String username) {
		if (getLoggedUser().equals(username)) {
			return fileDAO.getUserFiles(username);
		} else return null;
		
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public RevHostFile loadFile(String code) {
		RevHostFile file = fileDAO.getFile(code);
		
		if(getLoggedUser().equals(file.getUsername())
				|| file.getAccess().equals("public")) {
			return file;
		} else return null;
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public InputStream loadAsInputStream(String code) {
		RevHostFile revHostFile = fileDAO.getFile(code);

		if (!getLoggedUser().equals(revHostFile.getUsername())
				&& revHostFile.getAccess().equals("private")) {
			return null;
		}

		S3Object s3Object = s3client.getObject(bucketName, revHostFile.getKey());
		return s3Object.getObjectContent();
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public String getVideoUrl(String fileCode) {
		RevHostFile revHostFile = fileDAO.getFile(fileCode);
		if (!getLoggedUser().equals(revHostFile.getUsername())
				&& revHostFile.getAccess().equals("private")) {
			return null;
		}// TODO Throw exception instead
		
		// example of a key: storage/username/code/filename

				// making a link below
				// Date expiration = new Date();
				// long expTimeMilis = expiration.getTime();
				// expTimeMilis += 1000*60;
				// expiration.setTime(expTimeMilis);
				//
				// GeneratePresignedUrlRequest generatePresignedUrlRequest =
				// new GeneratePresignedUrlRequest(bucketName, key)
				// .withMethod(HttpMethod.GET)
				// .withExpiration(expiration);
				// URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
				// System.out.println(url);
		
		Date expiration = new Date();
		long expTimeMilis = expiration.getTime();
		expTimeMilis += 1000 * 60;
		expiration.setTime(expTimeMilis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = 
				new GeneratePresignedUrlRequest(revHostFile.getBucketName(), revHostFile.getKey())
				.withMethod(HttpMethod.GET)
				.withExpiration(expiration);
		URL url = s3client.generatePresignedUrl(generatePresignedUrlRequest);
		System.out.println(url);
		return url.toString();
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public boolean deleteFile(String fileCode) {
		RevHostFile file = fileDAO.getFile(fileCode);

		if (getLoggedUser().equals(file.getUsername())) {
			deleteFileFromS3Bucket(file);
			fileDAO.deleteFile(fileCode);
			return true;
		} else
			return false;
	}

	@Override
	@Transactional(value = "revhostTransactionManager")
	public boolean deleteAll(String username) {
		if (getLoggedUser().equals(username)) {
			return false;
		}

		List<String> codes = fileDAO.getUserFileCodes(username);
		codes.forEach(code -> deleteFile(code));

		return true;
	}

	private void deleteFileFromS3Bucket(RevHostFile file) {
		s3client.deleteObject(new DeleteObjectRequest(file.getBucketName(), 
				file.getKey()));

	}

}
