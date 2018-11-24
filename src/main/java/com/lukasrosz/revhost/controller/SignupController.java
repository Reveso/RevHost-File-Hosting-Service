package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.security.social.SignupForm;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private static final List<GrantedAuthority> DEFAULT_ROLES = Collections.
            singletonList(new SimpleGrantedAuthority("USER"));
    private final ProviderSignInUtils providerSignInUtils;
    private final UserDetailsManager userDetailsManager;

    @Autowired
    public SignupController(ProviderSignInUtils providerSignInUtils,
                            UserDetailsManager userDetailsManager){
        this.providerSignInUtils = providerSignInUtils;
        this.userDetailsManager = userDetailsManager;
    }

    @GetMapping
    public String signupForm(WebRequest request, Model model) {
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        SignupForm signupForm;
        if(connection != null) {
            if(connection.getApi().toString().contains("facebook")) {
                Facebook facebook = (Facebook) connection.getApi();
                String[] fields = {"id", "email", "first_name", "last_name"};
                User userProfile = facebook.fetchObject("me", User.class, fields);

                signupForm = new SignupForm(userProfile.getId(),
                        randomAlphabetic(20));
                val message = signupUser(signupForm, request);
                model.addAttribute("message", message);
                return "index";
            } else {
                UserProfile userProfile = connection.fetchUserProfile();
                signupForm = new SignupForm(userProfile.getUsername(),
                        randomAlphabetic(20));
                val message = signupUser(signupForm, request);
                model.addAttribute("message", message);
                return "index";
            }
        } else {
            signupForm = new SignupForm();
            model.addAttribute(signupForm);
            return "signup";
        }
    }

    @PostMapping
    public String signupUser(@Validated SignupForm form, BindingResult formBinding,
                         WebRequest request, Model model) {
        if(!formBinding.hasErrors()) {
            String message = signupUser(form, request);
            model.addAttribute("message", message);
            return "index";
        }
        return null;
    }

    private String encodePassword(String password) {
        val encoder = new BCryptPasswordEncoder();
        return "{bcrypt}" + encoder.encode(password);
    }

    private String signupUser(SignupForm form, WebRequest request) {
        SocialUser user = createUser(form);
        providerSignInUtils.doPostSignUp(user.getUsername(), request);
        return "You signed up successfully. You can now sign in.";
    }

    private SocialUser createUser(SignupForm form) {
        SocialUser user = new SocialUser(
                form.getUsername(), encodePassword(form.getPassword()), DEFAULT_ROLES);
        userDetailsManager.createUser(user);
        return user;
    }

    @ExceptionHandler(MySQLIntegrityConstraintViolationException.class)
    public ModelAndView mySqlIntegrityConstraintViolationExceptionHandler(){
        val modelAndView = new ModelAndView("signup");
        modelAndView.addObject("usernameTaken", true);
        modelAndView.addObject(new SignupForm());
        return modelAndView;
    }

    @InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
}
