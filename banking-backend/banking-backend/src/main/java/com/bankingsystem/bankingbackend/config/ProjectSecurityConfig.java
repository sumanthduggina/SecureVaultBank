package com.bankingsystem.bankingbackend.config;


import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AndRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.bankingsystem.bankingbackend.filter.AuthoritiesLoggingAfterFilter;
import com.bankingsystem.bankingbackend.filter.AuthoritiesLoggingAtFilter;
import com.bankingsystem.bankingbackend.filter.CsrfCookieFilter;
import com.bankingsystem.bankingbackend.filter.JWTTokenGenerationFilter;
import com.bankingsystem.bankingbackend.filter.JWTTokenValidatorFilter;
import com.bankingsystem.bankingbackend.filter.RequestValidationBeforeFilter;
import com.mysql.cj.Session;

@Configuration
public class ProjectSecurityConfig {
	
	 @Bean
	    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		 
		 CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		 requestHandler.setCsrfRequestAttributeName("-csrf");
		 
		 http.
		 //letting fraemwork to create jsession id after initial login completd  and same jsessionid is send to the UI application
		  sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().//sessionCreatedPolicy.STATELESS means don;t create any http,jsession sessions every thing is taker care by myself
		 cors().configurationSource(new CorsConfigurationSource() {
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration config = new CorsConfiguration();
				// allowing which domains or servers to communicate with it
				config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
				config.setAllowedMethods(Collections.singletonList("*"));
				config.setAllowCredentials(true);
				config.setAllowedHeaders(Collections.singletonList("*"));
//  the header name that we are going to send is "authorization" and inside the same header we are going to send the JWT token value. Since in this scenario, we are trying to expose an header
//from backend application to a different UI application which is hosted in another horizon.
				config.setExposedHeaders(Arrays.asList("Authorization"));
				config.setMaxAge(3600L);
				return config;
			}
			 
		 }).and(). csrf((csrf)->csrf.csrfTokenRequestHandler(requestHandler).ignoringRequestMatchers("/contact","/register")
				 .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
		 .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)//passing the filter to the framework
		 .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)//It will add the the own implemented filter just before the BasicAuthenticationFIlter
		 .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
		 .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class) 
		 .addFilterAfter(new JWTTokenGenerationFilter(), BasicAuthenticationFilter.class)
		 
			.authorizeHttpRequests().
			requestMatchers("/myAccount").hasRole("USER")
            .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")
            .requestMatchers("/myLoans").authenticated()
            .requestMatchers("/myCards").hasRole("USER")
            .requestMatchers("/user").authenticated()
            .requestMatchers("/notices","/contact","/register").permitAll()
			.and().formLogin()
			.and().httpBasic();
			return http.build();
	    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
