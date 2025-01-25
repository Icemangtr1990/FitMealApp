package es.dsw.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
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
				              .loginPage("/index")
				              .loginProcessingUrl("/logginprocess")
							  .defaultSuccessUrl("/index", true)
				              .permitAll()
				   )
		   
		.logout((logout) -> logout.logoutSuccessUrl("/bye").permitAll());
		
		return http.build();
	}
	
	
	@Bean
	InMemoryUserDetailsManager userDetailsService() {
			
		@SuppressWarnings("deprecation")
		UserDetails maria = User.withDefaultPasswordEncoder()
				                .username("maria")
				                .password("1234")
				                .roles("admin")
				                .build();
		
		@SuppressWarnings("deprecation")
		UserDetails pedro = User.withDefaultPasswordEncoder()
				                .username("pedro")
				                .password("5678")
				                .roles("secretario")
				                .build();
		
		
	
	
		InMeory = new InMemoryUserDetailsManager();
			
		InMeory.createUser(maria);
		InMeory.createUser(pedro);
					
		return InMeory;
	}
}