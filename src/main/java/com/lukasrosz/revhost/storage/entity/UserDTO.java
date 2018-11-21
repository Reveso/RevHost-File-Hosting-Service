package com.lukasrosz.revhost.storage.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
@NoArgsConstructor
@ToString
@Getter@Setter
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
	
}
