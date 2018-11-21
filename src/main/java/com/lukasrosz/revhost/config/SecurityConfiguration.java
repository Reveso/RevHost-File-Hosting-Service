package com.lukasrosz.revhost.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.lukasrosz.revhost.social.SimpleSocialUserDetailsService;
import com.lukasrosz.revhost.storage.entity.SecurityUserDTO;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private DataSource mainDataSource;

    @Autowired
    public SecurityConfiguration(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.jdbcAuthentication().dataSource(mainDataSource);
        auth.userDetailsService(userDetailsManager(null));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

//		http.authorizeRequests()
//			.antMatchers("/storage/**").authenticated()
//			.and()
//			.formLogin()
//				.loginPage("/users/login")
//				.loginProcessingUrl("/authenticateUser")
//				.failureUrl("/users/login-error")
//				.defaultSuccessUrl("/")
//				.permitAll()
//			.and()
//			.logout().permitAll()
//			.logoutSuccessUrl("/")
//			.and()
//			.exceptionHandling().accessDeniedPage("/users/access-denied")
//			.and()
//			.rememberMe().key("uniqueAndSecret");
        http.authorizeRequests()
                .antMatchers("/storage/**").authenticated()
                .and()
                    .formLogin()
                    .loginPage("/signin")
                    .failureUrl("/signin?param.error=bad_credentials")
                    .loginProcessingUrl("/signin/authenticate").permitAll()
                    .defaultSuccessUrl("/")
                .and()
                    .logout().logoutUrl("/signout").permitAll()
                    .logoutSuccessUrl("/");

        http.apply(new SpringSocialConfigurer());

//		http.authorizeRequests().anyRequest().authenticated();
    }

    @Bean
    @Primary
    public UserDetailsManager userDetailsManager(DataSource mainDataSource) {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
        jdbcUserDetailsManager.setDataSource(mainDataSource);
        jdbcUserDetailsManager.setEnableAuthorities(true);
        return jdbcUserDetailsManager;
    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService(
            UserDetailsService userDetailsService) {
        return new SimpleSocialUserDetailsService(userDetailsService);
    }
}
