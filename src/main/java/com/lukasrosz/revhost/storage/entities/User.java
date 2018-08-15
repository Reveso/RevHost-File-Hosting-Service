package com.lukasrosz.revhost.storage.entities;

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
public class User {

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
	private List<Authority> authorities;
	
//	@OneToMany(targetEntity=RevHostFile.class, mappedBy="user", fetch=FetchType.LAZY,
//			cascade=CascadeType.ALL)
	@OneToMany(targetEntity=RevHostFile.class, fetch=FetchType.LAZY,
	cascade=CascadeType.ALL)
	@JoinColumn(name="username")
	private List<RevHostFile> files;
			
	public User() {

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

	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

	
	public List<RevHostFile> getFiles() {
		return files;
	}

	public void setFiles(List<RevHostFile> files) {
		this.files = files;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", enabled=" + enabled +"]";
	}
	
}
