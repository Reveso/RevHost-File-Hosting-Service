package com.lukasrosz.revhost.storage.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.lukasrosz.revhost.storage.entity.FileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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
	public boolean store(MultipartFile multipartFile) throws Exception {
		String loggedUser = getLoggedUser();
		String fileCode = FileDTO.generateNewCode(fileDAO.findAllCodes());
		String filePath = "";
		final String tempPath = System.getProperty("java.io.tmpdir");
		System.out.println(tempPath);
		try {
			filePath = generateFilePath(multipartFile, fileCode, loggedUser);
			File file = convertMultipartToFile(multipartFile);

			uploadFileToS3Bucket(filePath, file);
			file.delete();
		} catch (IOException e) {
			throw e;
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
		fileDAO.save(fileDTO);
		return true;
	}

	private void uploadFileToS3Bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.Private));
	}

	private File convertMultipartToFile(MultipartFile file) throws IOException {
		final String tempPath = System.getProperty("java.io.tmpdir");
		System.out.println(tempPath);

		File convFile = new File(tempPath + file.getOriginalFilename());
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
	public List<FileDTO> loadLoggedUserFiles() {
		return fileDAO.findByUsername(getLoggedUser());
	}

	@Override
	public FileDTO loadFile(String fileCode) throws AccessDeniedException {
		FileDTO fileDTO = getFileFromDB(fileCode);
		if(fileDTO == null) return null;

		if(getLoggedUser().equals(fileDTO.getUsername())
				|| fileDTO.isPublicAccess()) {
			return fileDTO;
		} else throw new AccessDeniedException("Logged user is not file's owner and file is not public");
	}

	@Override
	public InputStream loadAsInputStream(String fileCode) throws AccessDeniedException {
		FileDTO fileDTO = getFileFromDB(fileCode);
		if(fileDTO == null) return null;

		if (!getLoggedUser().equals(fileDTO.getUsername())
				&& !fileDTO.isPublicAccess()) {
			throw new AccessDeniedException("Logged user is not file's owner and file is not public");
		}

		S3Object s3Object = s3client.getObject(bucketName, fileDTO.getKey());
		return s3Object.getObjectContent();
	}

	@Override
	public void deleteFile(String fileCode) throws AccessDeniedException {
		FileDTO fileDTO = getFileFromDB(fileCode);
		if(fileDTO == null) return;

		if (getLoggedUser().equals(fileDTO.getUsername())) {
			deleteFileFromS3Bucket(fileDTO);
			fileDAO.deleteById(fileCode);
		} else throw new AccessDeniedException("Logged user is not file's owner");
	}

	@Override
	public void deleteAll(String username) throws AccessDeniedException {
		if (!getLoggedUser().equals(username)) {
			throw new AccessDeniedException("Logged user is not file's owner");
		}

		List<String> codes = fileDAO.findCodesByUsername(username);
		for(String code : codes) {
			deleteFile(code);
		}
	}

	private void deleteFileFromS3Bucket(FileDTO file) {
		s3client.deleteObject(new DeleteObjectRequest(file.getBucketName(), 
				file.getKey()));
	}

    @Override
    public void setFileAccess(String fileCode, String access) throws AccessDeniedException {
		FileDTO fileDTO = getFileFromDB(fileCode);
		if(fileDTO == null) return;

		if(!getLoggedUser().equals(fileDTO.getUsername())) {
			throw new AccessDeniedException("Logged user is not file's owner");
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
		fileDAO.save(fileDTO);
	}

	private void setFilePublic(FileDTO fileDTO) {
		s3client.setObjectAcl(fileDTO.getBucketName(), fileDTO.getKey(), CannedAccessControlList.PublicRead);
		fileDTO.setPublicAccess(true);
		fileDAO.save(fileDTO);
	}

	private FileDTO getFileFromDB(String fileCode) {
		Optional<FileDTO> optionalFileDTO = fileDAO.findById(fileCode);
		return optionalFileDTO.isPresent() ? optionalFileDTO.get() : null;
	}
}
