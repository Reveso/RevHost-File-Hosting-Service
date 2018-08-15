package com.lukasrosz.revhost.storage.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.RandomStringUtils;

@Entity
@Table(name="files")
public class RevHostFile {
	
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
//	@Temporal(value=TemporalType.TIMESTAMP)
//	@Generated(value=GenerationTime.ALWAYS)
	private Date additionDate;
	
	@NotNull
	@Column(name="file_size")
	private long size;
	
	@NotNull
	@Column(name="file_type")
	private String type;
	
	@NotNull
	@Column(name="access")
	private String access;
	
//	@ManyToOne(cascade= 
//		{CascadeType.DETACH, CascadeType.MERGE, 
//				CascadeType.PERSIST, CascadeType.REFRESH},
//		fetch=FetchType.EAGER)
//	@JoinColumn(name="username")
	@NotNull
	@Column(name="username")
	private String username;

	public RevHostFile() {

	}
	
	public String getSizeAsString() {
		if(size <= Math.pow(10, 6)) {
			double sizeKB = size / (Math.pow(10, 3));
			return sizeKB + " KB";
		} else if (size <= Math.pow(1, 9)) {
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
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAdditionDate() {
		return additionDate;
	}

	public void setAdditionDate(Date additionDate) {
		this.additionDate = additionDate;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "RevHostFile [code=" + code + ", bucketName=" + bucketName + ", key=" + key + ", name=" + name
				+ ", additionDate=" + additionDate + ", size=" + size + ", type=" + type + ", access=" + access
				+ ", username=" + username + "]";
	}
	
}
