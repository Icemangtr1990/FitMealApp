package es.dsw.app;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.dsw.models.Users;
import es.dsw.services.UsuariosService;
import jakarta.annotation.PostConstruct;

/**
 * Convierte a BCrypt las contraseñas en texto plano del SQL.txt durante el
 * arranque. Solo actúa la primera vez: si la contraseña ya empieza por $2a$,
 * $2b$ o $2y$ se deja tal cual.
 */
@Component
public class PasswordMigrationService {

	private final UsuariosService usuariosService;
	private final PasswordEncoder passwordEncoder;

	public PasswordMigrationService(UsuariosService usuariosService, PasswordEncoder passwordEncoder) {
		this.usuariosService = usuariosService;
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void migratePlainTextPasswords() {
		try {
			List<Users> usuarios = usuariosService.findAll();

			for (Users usuario : usuarios) {
				String hash = usuario.getPasswd();
				if (hash == null || (!hash.startsWith("$2a$")
						&& !hash.startsWith("$2b$")
						&& !hash.startsWith("$2y$"))) {
					String plano = (hash == null || hash.isBlank()) ? "1234" : hash;
					usuariosService.actualizarPassword(usuario.getIdUser(), passwordEncoder.encode(plano));
				}
			}
		} catch (Exception e) {
			// En los tests la tabla puede no existir todavía: no es un error fatal.
			System.err.println("No se pudo comprobar la migración de contraseñas: " + e.getMessage());
		}
	}

}
