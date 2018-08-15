package com.lukasrosz.revhost;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private DataSource revhostDataSource;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.jdbcAuthentication().dataSource(revhostDataSource);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
			.antMatchers("/storage/**").authenticated()
			.and()
			.formLogin()
				.loginPage("/users/login")
				.loginProcessingUrl("/authenticateUser")
				.failureUrl("/users/login-error")
				.defaultSuccessUrl("/")
				.permitAll()
			.and()
			.logout().permitAll()
			.logoutSuccessUrl("/")
			.and()
			.exceptionHandling().accessDeniedPage("/users/access-denied")
			.and()
			.rememberMe().key("uniqueAndSecret");
	}
	
	@Bean
	public UserDetailsManager userDetailsManager() {
		
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
		jdbcUserDetailsManager.setDataSource(revhostDataSource);
		
		return jdbcUserDetailsManager;
	}

}
