package com.example.exceptionhandling;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		// getting dynamic value
		LocalDateTime currentTimeStamp=LocalDateTime.now();
		String message=(authException!=null && authException.getMessage()!=null)?authException.getLocalizedMessage():"Unauthorized";
		String path=request.getRequestURI();
		
		
		// this two line i have taken from BasicAuthenticationEntry point class which is implementation of AuthenticationEntryPoint
		response.setHeader("eazybank-error-reason", "Authentication failed"); // cutomization of our own response header i postman
//        response.setHeader("WWW-Authenticate", "Basic realm=\"EazyBank\""); // bydefault response header  of postman
		
		// this line is applicable for non customization of json respone
//		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase()); 
		// whenever we write upper line  , spring security framework it is not going to consider what custom json response 
		//  we are trying  to send (here we are  making our own custom response body) so we have to invoke response.setStatus method  
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		// Construct the JSON response
		response.setContentType("application/json;charset=UTF-8");
		String jsonResponse=String.format("{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}"
		,currentTimeStamp,HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.getReasonPhrase(),message,path);
		
		response.getWriter().write(jsonResponse); 
	}

}
