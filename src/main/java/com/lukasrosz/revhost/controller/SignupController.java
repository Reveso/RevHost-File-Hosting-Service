package com.lukasrosz.revhost.controller;

import com.lukasrosz.revhost.social.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SocialUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.List;

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
    public SignupForm signupForm(WebRequest request) {
        System.out.println("TUUUUUUUUUUUUUUUUUUUUU");
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
        if(connection != null) {
            System.out.println("22TUUUUUUUUUUUUUUUUUUUUU");
            return SignupForm.fromProviderUser(connection.fetchUserProfile());
        } else {
            return new SignupForm();
        }
    }

    @PostMapping
    public String signup(@Validated SignupForm form, BindingResult formBinding,
                         WebRequest request) {
        if(!formBinding.hasErrors()) {
            SocialUser user = createUser(form);
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(user.getUsername(),
                            null, user.getAuthorities()));
            providerSignInUtils.doPostSignUp(user.getUsername(), request);
            return "redirect:/";
        }
        return null;
    }

    private SocialUser createUser(SignupForm form) {
        SocialUser user = new SocialUser(
                form.getUsername(), form.getPassword(), DEFAULT_ROLES);
        userDetailsManager.createUser(user);
        return user;
    }
}
