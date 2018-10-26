package com.lukasrosz.revhost.storage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import com.lukasrosz.revhost.storage.entity.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.lukasrosz.revhost.exception.AccessToFileDeniedException;
import com.lukasrosz.revhost.storage.dao.FileDAO;

@Service
public class AmazonS3StorageService implements StorageService {

	private AmazonS3 s3client;
		
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
	public AmazonS3StorageService(FileDAO fileDAO) {
		this.fileDAO = fileDAO;
	}
	
	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		this.s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}

	@Override
	@Transactional
	public boolean store(MultipartFile multipartFile) {
		String loggedUser = getLoggedUser();
		String fileCode = FileDTO.generateNewCode(fileDAO.getAllFileCodes());
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

		FileDTO fileDTO = new FileDTO();
		fileDTO.setCode(fileCode);
		fileDTO.setBucketName(bucketName);
		fileDTO.setKey(filePath);
		fileDTO.setName(multipartFile.getOriginalFilename().replace(" ", "_"));

		fileDTO.setSize(multipartFile.getSize());
		fileDTO.setPublicAccess(false);
		fileDTO.setUsername(getLoggedUser());

		fileDTO.setAdditionDate(new Date());
		fileDTO.setUrl(s3client.getUrl(bucketName, filePath).toString());

		System.out.println(fileDTO);
		fileDAO.saveFile(fileDTO);
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
	@Transactional
	public List<FileDTO> loadLoggedUserFiles() {
	    return fileDAO.getUserFiles(getLoggedUser());
	}

	@Override
	@Transactional
	public FileDTO loadFile(String code) throws AccessToFileDeniedException {
		FileDTO file = fileDAO.getFile(code);
		
		if(getLoggedUser().equals(file.getUsername())
				|| file.isPublicAccess()) {
			return file;
		} else throw new AccessToFileDeniedException();
	}

	@Override
	@Transactional
	public InputStream loadAsInputStream(String code) throws AccessToFileDeniedException {
		FileDTO fileDTO = fileDAO.getFile(code);

		if (!getLoggedUser().equals(fileDTO.getUsername())
				&& !fileDTO.isPublicAccess()) {
			throw new AccessToFileDeniedException();
		}

		S3Object s3Object = s3client.getObject(bucketName, fileDTO.getKey());
		return s3Object.getObjectContent();
	}

	@Override
	@Transactional
	public void deleteFile(String fileCode) throws AccessToFileDeniedException {
		FileDTO file = fileDAO.getFile(fileCode);

		if (getLoggedUser().equals(file.getUsername())) {
			deleteFileFromS3Bucket(file);
			fileDAO.deleteFile(fileCode);
		} else throw new AccessToFileDeniedException("Logged user is not file's owner");
	}

	@Override
	@Transactional
	public void deleteAll(String username) throws AccessToFileDeniedException {
		if (!getLoggedUser().equals(username)) {
			throw new AccessToFileDeniedException("Logged user is not file's owner");
		}

		List<String> codes = fileDAO.getUserFileCodes(username);
		for(String code : codes) {
			deleteFile(code);
		}
	}

	private void deleteFileFromS3Bucket(FileDTO file) {
		s3client.deleteObject(new DeleteObjectRequest(file.getBucketName(), 
				file.getKey()));
	}

    @Override
    public void setFileAccess(String fileCode, String access) throws AccessToFileDeniedException {
		FileDTO fileDTO = fileDAO.getFile(fileCode);
		if(!getLoggedUser().equals(fileDTO.getUsername())) {
			throw new AccessToFileDeniedException("Logged user is not file's owner");
		}

		if(access.equals("private")) {
			setFilePrivate(fileDTO);
		} else if (access.equals("public")) {
			setFilePublic(fileDTO);
		}
	}

	private void setFilePrivate(FileDTO fileDTO) {
		s3client.setObjectAcl(fileDTO.getBucketName(), fileDTO.getKey(), CannedAccessControlList.Private);
		fileDTO.setPublicAccess(false);
		fileDAO.saveFile(fileDTO);
	}

	private void setFilePublic(FileDTO fileDTO) {
		s3client.setObjectAcl(fileDTO.getBucketName(), fileDTO.getKey(), CannedAccessControlList.PublicRead);
		fileDTO.setPublicAccess(true);
		fileDAO.saveFile(fileDTO);
	}
}
