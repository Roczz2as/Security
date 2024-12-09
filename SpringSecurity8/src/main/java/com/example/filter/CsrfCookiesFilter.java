package com.example.filter;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CsrfCookiesFilter extends OncePerRequestFilter{
 
	// how to read CSRF Token manually this is the code
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// whateever the csrk toekn is store in request it return the object of CsrfToken
	        CsrfToken  csrfToken=(CsrfToken)request.getAttribute(CsrfToken.class.getName());
	   //here actuall token generation is happening, once the token  is generated  the spring seurity is going to take care of
	        //sending the same as part of cookie as well
	        
//	        The {@link CsrfToken} is available as a request attribute named
//	   	 * {@code CsrfToken.class.getName()}. By default, an additional request attribute that
//	   	 * is the same as {@link CsrfToken#getParameterName()} is set. This attribute allows
//	   	 * overriding the additional attribute.
//	   	 * @param csrfRequestAttributeName the name of an additional request attribute with
//	   	 * the value of the CsrfToken. Default is {@link CsrfToken#getParameterName()}
	        csrfToken.getToken();
	        
	        filterChain.doFilter(request,response);
		
	}

}
