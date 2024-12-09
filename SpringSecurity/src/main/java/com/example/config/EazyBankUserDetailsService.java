package com.example.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.model.Customer;
import com.example.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service

// if we write @Service at the top of EazyBankUserDetailsService behind the scenes springBoot during the starter it is going to create
// bean of this class , since it is implementing UserDetailsService , the return type of EazyBankUserDetailsService class is going to 
// be UserDetailsService , my spring security have a clue this fellow created a custom userDetailsService implementation and i have to
// use same during the authentication process
public class EazyBankUserDetailsService implements UserDetailsService {
   
	@Autowired
	private  CustomerRepository customerRepository;
	 
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 
 Customer customer=customerRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User details not found for the user:" + username));
		// we have convert role(datatype)  into grantedAuthority
  List<GrantedAuthority> authorities=List.of(new SimpleGrantedAuthority(customer.getRole()));
             // Create and return a User object
         return new User(customer.getEmail(),customer.getPwd(),authorities);
         
         
	}

	
} 
