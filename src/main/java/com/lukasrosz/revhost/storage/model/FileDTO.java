package com.lukasrosz.revhost.storage.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Entity
@Table(name="files")
@Getter@Setter
@NoArgsConstructor
@ToString
public class FileDTO {
	
	@Id
	@Column(name="file_code")
	private String code;
	
	@NotNull
	@Column(name="bucket_name")
	private String bucketName;

	@NotNull
	@Column(name="file_key")
	private String key;
	
	@NotNull
	@Column(name="file_name")
	private String name;
	
	@Column(name="addition_date", nullable=false)
	@Generated(value= GenerationTime.INSERT)
	private Date additionDate;
	
	@NotNull
	@Column(name="file_size")
	private long size;

	@NotNull
	@Column(name="public_access")
	private boolean publicAccess;

	@NotNull
	@Column(name="username")
	private String username;

	@NotNull
	@Column(name="url")
	private String url;

	public String getSizeAsString() {
		if(size <= Math.pow(10, 6)) {
			double sizeKB = size / (Math.pow(10, 3));
			return sizeKB + " KB";
		} else if (size <= Math.pow(10, 9)) {
			double sizeMB = size / (Math.pow(10, 6));;
			return sizeMB + " MB";
		} else {
			double sizeGB = size / (Math.pow(10, 9));;
			return sizeGB + " GB";
		}
	}
	
	public static String generateNewCode(List<String> codes) {	
		int length = 10;
	    boolean useLetters = true;
	    boolean useNumbers = true;
	    String generatedCode = RandomStringUtils.random(length, useLetters, useNumbers);
	 
	    if(codes.contains(generatedCode)) {
	    	return generateNewCode(codes);
	    } else {
	    	return generatedCode;
	    }
	}
}
