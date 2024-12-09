package com.example.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.exceptionhandling.CustomAccessDeniedHandler;
import com.example.exceptionhandling.CustomBasicAuthenticationEntryPoint;
import com.example.filter.CsrfCookiesFilter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
@Profile("!prod")

public class ProjectSecurityConfig {
          
	@Bean
	@Order(SecurityProperties.BASIC_AUTH_ORDER)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		//this is going to help me to read the csrf token value that i am receiving inside the request header from UI once the token
		// value is read teh same is going to populated as part of a request attribute
		CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler= new CsrfTokenRequestAttributeHandler();
		
		// write now we are not using the spring security provided login page, we are having our own login page, where we are trying
		// to use http basic style of authentication . in this kind of scenerio we are going to spring security to generate jsessionId
		// for me and stored it inside the securityContext  folder
		http.securityContext(contextConfig->contextConfig.requireExplicitSave(false))
		.sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
		
		.cors(corsConfig->corsConfig.configurationSource( new CorsConfigurationSource() {
			
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration config = new CorsConfiguration();
				// providing the connection path of UI application
				config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
				//with this line i am trying to  allowing all  type of http method traffic
				config.setAllowedMethods(Collections.singletonList("*"));
				// this setAllowCredentials method we are trying to enable accepting the user credential("/user") or any other
				//applicable cookiesfrom the UI origin to backend server 
				config.setAllowCredentials(true);
                  
				// i am find to accept all kind of header
				config.setAllowedHeaders(Collections.singletonList("*"));
				config.setMaxAge(3600L);

				return config;
			}
		}))
	//	1] * A {@link CsrfTokenRepository} that persists the CSRF token in a cookie named
	//	 * "XSRF-TOKEN" and reads from the header "X-XSRF-TOKEN" following the conventions of
	// * AngularJS. When using with AngularJS be sure to use {@link #withHttpOnlyFalse()}.
		
	//2] 	Factory method to conveniently create an instance that creates cookies where
	//	 * {@link Cookie#isHttpOnly()} is set to false.
	//	 * @return an instance of CookieCsrfTokenRepository that creates cookies where
		// * {@link Cookie#isHttpOnly()} is set to false.
		.csrf(csrfConfig->csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
				.ignoringRequestMatchers("/contact","register")
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
		
		.addFilterAfter(new CsrfCookiesFilter(),BasicAuthenticationFilter.class)
         //.sessionManagement(smc->smc.invalidSessionUrl("/invalidSession").maximumSessions(3).maxSessionsPreventsLogin(true))
 
		.requiresChannel(rcc->rcc.anyRequest().requiresInsecure()) // this line  accept only HTTP Request
		// we cant write directly write csc.disable(), because it is  not reccommended for production application
		//.csrf(csc->csc.disable())
	.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/myAccounts","/myBalance","/myLoans","/myCards","/user").authenticated()
				.requestMatchers("/notices","/contact","/register","/invalidSession").permitAll());
		// whenever we enable formLogin in our webApplication , the extract credential from  
		// request is going to taken care by UsernamePasswordAuthenticationFilter()
		// httpFormLogin () uses Login form as an starting  
		http.formLogin(Customizer.withDefaults());
		// if i only use http.httpBasic instead of http.formLogin,  it will show Alert Box instead of formLogin page
//		http.httpBasic(Customizer.withDefaults());
		
		// this is line is used to communicate this entrypoint(CustomBasicAuthenticationEntryPoint) with spring security
		http.httpBasic(hbc->hbc.authenticationEntryPoint(new  CustomBasicAuthenticationEntryPoint()));
		
		// use http.exceptionHandling instead of http.httpBasic in production apllication to communicate 
		//this entrypoint(CustomBasicAuthenticationEntryPoint) with spring security
		
       //http.exceptionHandling(hbc->hbc.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint())); //  It is an Global Config
		
		// this is line is used to communicate this entrypoint(CustomAccessDeniedHandler) with spring security

       http.exceptionHandling(ehc->ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
       // http.build() method is going to take care of building all the configuration and returing an object of SecurityFilterChain
		return http.build();
	 }

	
	// to create user manually we use UserDetailsService
//	@Bean
//	public UserDetailsService userDetailsService() {
//		// build () method is used to make sure that this line of code return as an  UserDetails() object
//	  UserDetails user =User.withUsername("user").password("{noop}12345").authorities("read").build();
//	  UserDetails admin=User.withUsername("admin").password("{bcrypt}$2a$12$1IKu9vqU/ev4lRfNO1PTEeqTVd1ywhYmbh2lpJzCjSMb0DElZWtBa")
//			  .authorities("admin").build();
//	    
//	  // InMemoryUserDetailsManager accept UserDetails as an argument 
//	  // we have to return one of the UserDetailsService method (which is InMemoryUserDetailsManager)  
//	  return new  InMemoryUserDetailsManager(user,admin);
// 
//	}
	 
//	public UserDetailsService userDetailsService(DataSource dataSource) {
//		return new JdbcUserDetailsManager(dataSource);
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// CompromisedPasswordChecker always take strong password
	@Bean
	public CompromisedPasswordChecker compromisedPasswordChecker() {
		return new HaveIBeenPwnedRestApiPasswordChecker();
	}
}
