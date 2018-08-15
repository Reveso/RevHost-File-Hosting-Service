package com.lukasrosz.revhost.controllers;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String showHomePage() {

		return "index";
	}
	
	@GetMapping("/whatUser")
	public String sshowHomePage() {
		System.out.println("==================>>>>|" + getLoggedUser());
		return "redirect:/";
	}
	
	private String getLoggedUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

}
