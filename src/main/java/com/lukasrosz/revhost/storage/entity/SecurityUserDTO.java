package com.lukasrosz.revhost.storage.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SecurityUserDTO {

	@NotNull(message="is required")
	@Size(min=1, message="is required")
	private String username;
	
	@NotNull(message="is required")
	@Size(min=1, message="is required")
	private String password;
	
	public SecurityUserDTO() {

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

	@Override
	public String toString() {
		return "SecurityUserDTO [username=" + username + ", password=" + password + "]";
	}
	
}