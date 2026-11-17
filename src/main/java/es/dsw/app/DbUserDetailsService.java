package es.dsw.app;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.dsw.models.Users;
import es.dsw.services.UsuariosService;

/**
 * Servicio de autenticación que consulta la BD en cada login a través de la
 * capa de servicios.
 */
@Service
public class DbUserDetailsService implements UserDetailsService {

	private final UsuariosService usuariosService;

	public DbUserDetailsService(UsuariosService usuariosService) {
		this.usuariosService = usuariosService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users usuario = usuariosService.findByUsername(username);

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
