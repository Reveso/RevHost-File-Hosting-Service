package com.lukasrosz.revhost.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="authorities")
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDTO {
	
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

	@Override
	public String toString() {
		return authority;
	}
}
