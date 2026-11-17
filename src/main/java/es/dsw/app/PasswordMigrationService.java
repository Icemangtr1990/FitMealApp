package es.dsw.app;

import java.util.ArrayList;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.dsw.models.Users;
import jakarta.annotation.PostConstruct;

/**
 * Convierte a BCrypt las contraseñas en texto plano del SQL.txt durante el
 * arranque. Solo actúa la primera vez: si la contraseña ya empieza por $2a$,
 * $2b$ o $2y$ se deja tal cual.
 */
@Component
public class PasswordMigrationService {

	private final PasswordEncoder passwordEncoder;

	public PasswordMigrationService(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@PostConstruct
	public void migratePlainTextPasswords() {
		ArrayList<Users> usuarios = new Users().getUser();

		for (Users usuario : usuarios) {
			String hash = usuario.getPasswd();
			if (hash == null || (!hash.startsWith("$2a$")
					&& !hash.startsWith("$2b$")
					&& !hash.startsWith("$2y$"))) {
				String plano = (hash == null || hash.isBlank()) ? "1234" : hash;
				new Users().actualizarPassword(usuario.getIdUser(), passwordEncoder.encode(plano));
			}
		}
	}

}
