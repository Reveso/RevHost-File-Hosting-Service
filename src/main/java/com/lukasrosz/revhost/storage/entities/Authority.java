package com.lukasrosz.revhost.storage.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="authorities")
public class Authority {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id",unique=true, nullable = false)
	private Long id;
	
	@NotNull
	@Column(name="username")
	private String username;
	
	@NotNull
	@Column(name="authority")
	private String authority;
	
	
	public Authority() {
		
	}

	public Authority(@NotNull String username, @NotNull String authority) {
		this.username = username;
		this.authority = authority;
	}
	
	public Authority(@NotNull String authority) {
		this.authority = authority;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@Override
	public String toString() {
		return authority;
	}

}
