package com.lukasrosz.revhost.storage.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="users")
public class UserDTO {

	@Id
	@NotNull
	@Column(name="username")
	private String username;
	
	@NotNull
	@Column(name="password")
	private String password;

	@NotNull
	@Column(name="enabled")
	private boolean enabled;
	
	@OneToMany(mappedBy="username", fetch=FetchType.LAZY,
			cascade=CascadeType.ALL)
	private List<AuthorityDTO> authorities;

	@OneToMany(targetEntity= FileDTO.class, fetch=FetchType.LAZY,
	cascade=CascadeType.ALL)
	@JoinColumn(name="username")
	private List<FileDTO> files;
			
	public UserDTO() {

	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public List<AuthorityDTO> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<AuthorityDTO> authorities) {
		this.authorities = authorities;
	}

	
	public List<FileDTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileDTO> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "UserDTO [username=" + username + ", password=" + password + ", enabled=" + enabled +"]";
	}
	
}
