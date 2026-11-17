package es.dsw.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
		   .authorizeHttpRequests((authorize) -> authorize
					    			  .requestMatchers("/css/**").permitAll()
							  .requestMatchers("/images/**").permitAll()
					    			  .requestMatchers("/index").permitAll()
					    			  .requestMatchers("/loggin").permitAll()
							  .requestMatchers("/admin/**").hasRole("admin")
							  .requestMatchers("/agregarReceta/**").hasRole("admin")
							  .requestMatchers("/crearUsuario/**").hasRole("admin")
					    			  .requestMatchers("/index/**").permitAll()
					    			  .anyRequest().authenticated()
				                 )
		   .httpBasic(withDefaults())
		   .formLogin(form -> form
					              .loginPage("/index")
					              .loginProcessingUrl("/logginprocess")
							  .defaultSuccessUrl("/index", true)
							  .failureUrl("/loggin?error=true")
					              .permitAll()
				   )
		   .logout((logout) -> logout.logoutSuccessUrl("/index").permitAll());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
