package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.security.model.UserDTO;
import com.lukasrosz.revhost.security.dao.UserDAO;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/profile")
    public String profileSettings(Model model) throws AccessDeniedException {
        Optional<UserDTO> userOptionalDTO = userDAO.findById(getLoggedUser());

        if (userOptionalDTO.isPresent()) {
            val userDTO = userOptionalDTO.get();
            model.addAttribute("user", userDTO);

            System.out.println(userDTO.getUserDetails().getUsername());
            System.out.println(userDTO.getUserDetails().isSocialConnected());
        } else throw new AccessDeniedException("User not exists");

        return "user-profile";
    }

    private String getLoggedUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
