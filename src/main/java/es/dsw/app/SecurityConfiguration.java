package es.dsw.app;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import es.dsw.models.Users;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	public static InMemoryUserDetailsManager InMeory;
    
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
		   .authorizeHttpRequests((authorize) -> authorize
				    			  .requestMatchers("/css/**").permitAll()
				    			  .requestMatchers("/index").permitAll()
				    			  .requestMatchers("/loggin").permitAll()
				    			  .requestMatchers("/index/**").hasRole("admin")
				    			  .anyRequest().authenticated()
				                 )
		   .httpBasic(withDefaults())
		   .formLogin(form -> form
				              .loginPage("/loggin")
				              .loginProcessingUrl("/logginprocess")
							  .defaultSuccessUrl("/index", true)
							  
				              .permitAll()
				   )
		   
		.logout((logout) -> logout.logoutSuccessUrl("/loggin").permitAll());
		
		return http.build();
	}
	
	
	@Bean
	 InMemoryUserDetailsManager userDetailsService() {

		InMeory = new InMemoryUserDetailsManager();

		

		ArrayList<Users> objListaUsuarios  = new Users().getUser();	
		System.out.println(objListaUsuarios);
	 	for(Users usuario : objListaUsuarios) {
		 	@SuppressWarnings("deprecation")
		 	UserDetails user = User.withDefaultPasswordEncoder()
		 					   .username(usuario.getNombre())
		 					   .password(usuario.getPasswd())
		 					   .roles(usuario.getUserRole())
		 		               .build();
		 InMeory.createUser(user);
			System.out.println(usuario.getNombre());

		}
		return InMeory;
	}
	
}

			
	// 	@SuppressWarnings("deprecation")
	//  	UserDetails maria = User.withDefaultPasswordEncoder()
	//  			                .username("maria")
	//  			                .password("1234")
	//  			                .roles("admin")
	//  			                .build();
		
	//  	@SuppressWarnings("deprecation")
	// 	UserDetails pedro = User.withDefaultPasswordEncoder()
	// 			                .username("pedro")
	//  			                .password("5678")
	//  			                .roles("secretario")
	//  			                .build();
		
		
	
	
		
			
	//  	InMeory.createUser(maria);
	//  	InMeory.createUser(pedro);
					
	//  	return InMeory;
	//  }

	// @Bean
	// InMemoryUserDetailsManager userDetailsService() 
	// { 
	// 	InMemory =  new InMemoryUserDetailsManager(); 
		
	// 	ArrayList<Users> objListaUsuarios  = new Users().getUser();	
		
	// 	for(Users usuario : objListaUsuarios) {
			
			

	// 		@SuppressWarnings("deprecation")
	// 		UserDetails user = User.withDefaultPasswordEncoder()
	// 							   .username(usuario.getNombre())
	// 							   .password(usuario.getPasswd())
	// 							   .roles(usuario.getUserRole())
	// 				               .build();
	// 		InMemory.createUser(user);
	// 	}	
		
	// 	return InMemory;
	// }
