package com.example.exceptionhandling;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		LocalDateTime currentTimeStamp=LocalDateTime.now();
		String message=(accessDeniedException!=null && accessDeniedException.getMessage()!=null)?accessDeniedException
				.getLocalizedMessage():"Authorization failed";
				
		String path=request.getRequestURI();
		
		
		// this two line i have taken from BasicAuthenticationEntry point class which is implementation of AuthenticationEntryPoint
		response.setHeader("eazybank-denied-reason", "Authorization  failed"); // cutomization of our own response header i postman
//        response.setHeader("WWW-Authenticate", "Basic realm=\"EazyBank\""); // bydefault response of postman
		
		// this line is applicable for non customization of json respone
//		response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase()); 
		// whenever we write upper line  , spring security framework it is not going to consider what custom json response 
		//  we are trying  to send (here we are  making our own custom response body) so we have to invoke response.setStatus method  
		response.setStatus(HttpStatus.FORBIDDEN.value());
		// Construct the JSON response
		response.setContentType("application/json;charset=UTF-8");
		String jsonResponse=String.format("{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}"
		,currentTimeStamp,HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN.getReasonPhrase(),message,path);
		
		response.getWriter().write(jsonResponse); 		
	}

}
