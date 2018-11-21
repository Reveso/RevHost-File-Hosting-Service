package com.lukasrosz.revhost.storage.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter @Setter
@ToString
public class SecurityUserDTO {

	@NotNull(message="is required")
	@Size(min=1, message="is required")
	private String username;
	
	@NotNull(message="is required")
	@Size(min=1, message="is required")
	private String password;

}