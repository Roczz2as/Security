package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class EazyBankUsernamePwdAuthentication implements AuthenticationProvider {
                
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username=authentication.getName();
		String pwd=authentication.getCredentials().toString();
		// this loadUserByUsername is the EazyBankUserDetailsService(and has method loadUserByUsername which return object of UserDetails)
		// which implements UserDetailsService
		
		
		// if the username is not present it return exception of  UsernameNotFoundException("User details not found for the user:" + username)
	    // here we are accepting any kind of password 
		UserDetails userDetails=userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(username,pwd,userDetails.getAuthorities());
//	        if(passwordEncoder.matches(pwd, userDetails.getPassword())) {
//	        	// fetch Age details and perform validation to check if age>18
//	        	return new UsernamePasswordAuthenticationToken(username,pwd,userDetails.getAuthorities());
//	        }
//	        else {
//	        	throw new BadCredentialsException("Invalid password");
//	        }
	}

	@Override 
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
