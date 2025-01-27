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
								  .requestMatchers("/recetas/**").permitAll()
								  .requestMatchers("/agregarReceta").permitAll()
								  .requestMatchers("/agregarReceta/**").hasRole("admin")
				    			  .requestMatchers("/index/**").permitAll()
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
			System.out.println(usuario.getUserRole());

		}
		return InMeory;
	}
	
}

	
