package com.lukasrosz.revhost.social;

import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.User;

public class SignupForm {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static SignupForm fromProviderUser(UserProfile providerUser) {
        SignupForm form = new SignupForm();
        form.setUsername(providerUser.getId());
        return form;
    }
}
