package es.dsw.app;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.dsw.models.Users;

/**
 * Servicio de autenticación que consulta la base de datos en cada intento de
 * login. A diferencia del InMemoryUserDetailsManager, los usuarios creados o
 * eliminados desde la interfaz surten efecto sin reiniciar la aplicación.
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users usuario = new Users().getUserByUsername(username);

		if (usuario == null) {
			throw new UsernameNotFoundException("Usuario no encontrado: " + username);
		}

		String hash = usuario.getPasswd();
		if (hash == null || hash.isBlank()) {
			throw new UsernameNotFoundException("El usuario no tiene una contraseña válida: " + username);
		}

		String rol = usuario.getUserRole();
		if (rol == null || rol.isBlank()) {
			rol = "usuario";
		}

		return User.withUsername(usuario.getNombre())
				.password(hash)
				.roles(rol)
				.build();
	}

}
