package com.loginAPI.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.loginAPI.filter.LoginAuthenticationFilter;
import com.loginAPI.oauth.PrincipalSocialOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalSocialOAuth2UserService principalSocialOAuth2UserService;
	
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.formLogin().disable()
		.httpBasic().disable()
		.addFilter(new LoginAuthenticationFilter(authenticationManager()))
		.authorizeRequests()
		.anyRequest().permitAll()
		.and()
		.oauth2Login()
		.userInfoEndpoint()
		.userService(principalSocialOAuth2UserService);

	}
}
