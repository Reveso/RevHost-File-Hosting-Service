package com.lukasrosz.revhost.controllers;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lukasrosz.revhost.storage.entities.helperentities.SecurityUser;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/users")
public class UsersController {

	@Autowired
	private UserDetailsManager userDetailsManager;
	
	@GetMapping("/login")
	public String showLoginPage() {
		return "login-form";
	}
	
	@GetMapping("/login-error")
	public String loginError(Model model) {
		model.addAttribute("loginError", true);
		return "login-form";
	}
	  
	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {
		
		model.addAttribute("newUser", new SecurityUser());
		return "registration-form";
	}
	
	@PostMapping("/processRegistrationForm")
	public String processLogin(@Valid @ModelAttribute("newUser") SecurityUser newUser,
								BindingResult bindingResult, Model model) {

		if(bindingResult.hasErrors()) {
			model.addAttribute("registrationError", "Username/Password cannot be empty");
			return "registration-form";
		}
		
		if(userExists(newUser)) {
			model.addAttribute("registrationError", "User already exists");
			return "registration-form";
		}
		
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(newUser.getPassword());
		
		encodedPassword = "{bcrypt}" + encodedPassword;
		
		List<GrantedAuthority> authorities = AuthorityUtils
												.createAuthorityList("ROLE_UNREGISTERED");
		
		org.springframework.security.core.userdetails
		.User tempUser = new org.springframework.security.core.userdetails
				.User(newUser.getUsername(), encodedPassword, authorities);
				
		userDetailsManager.createUser(tempUser);
		
		return "redirect:/";
	}
	
	private boolean userExists(SecurityUser securityUser) {
		return userDetailsManager.userExists(securityUser.getUsername());
	}
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}

}
