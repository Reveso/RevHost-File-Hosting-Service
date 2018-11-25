package com.lukasrosz.revhost.security.social;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignupForm {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

}
