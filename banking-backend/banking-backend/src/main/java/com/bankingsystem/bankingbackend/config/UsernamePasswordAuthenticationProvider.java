package com.bankingsystem.bankingbackend.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.bankingsystem.bankingbackend.model.Customer;
import com.bankingsystem.bankingbackend.model.Authority;
import com.bankingsystem.bankingbackend.repository.CustomerRepository;

//Implementing our own Authentication provider
@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider{

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	//in this method we have to write the code for authentication logic, loading user details from the storage 
	//system comparing the pawds, post that create the successful authentication object with an information 
	//of authnecication is successful or not.
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		//Load the end User user-name and password
		String username = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		List<Customer> customer = customerRepository.findByEmail(username);
		if(customer.size()>0) {
			if(passwordEncoder.matches(pwd, customer.get(0).getPwd())) {
				
				return new UsernamePasswordAuthenticationToken(username, pwd, getGrantedAuthorities(customer.get(0).getAuthorities()));
			}
			else {
				 throw new BadCredentialsException("Invalid password");
			}
		}
		else {
			throw new BadCredentialsException("No User registered weith the details");
		}
	}
	private List<GrantedAuthority> getGrantedAuthorities(Set<Authority> authorities){
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		for(Authority authority: authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
		}
		return grantedAuthorities;
		
	}

	//check whether user name and password authentication method is supports or not.
	@Override
	public boolean supports(Class<?> authentication) {
		
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}
	
	

}
