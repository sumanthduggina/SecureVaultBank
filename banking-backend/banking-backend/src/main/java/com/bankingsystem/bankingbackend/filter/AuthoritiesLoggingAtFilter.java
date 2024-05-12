package com.bankingsystem.bankingbackend.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.util.logging.Logger;
public class AuthoritiesLoggingAtFilter implements Filter{


    private final Logger LOG =
            Logger.getLogger(AuthoritiesLoggingAtFilter.class.getName());
    
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOG.info("Authentication Validation is in progress");
        chain.doFilter(request, response);
	}

}
