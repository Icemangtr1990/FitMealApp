package es.dsw.app;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordEncoderTests {

	@Test
	void bcryptHashMatchesRawPassword() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode("1234");

		assertNotEquals("1234", hash, "La contraseña no debe almacenarse en texto plano");
		assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$"), "Debe ser un hash BCrypt");
		assertTrue(encoder.matches("1234", hash), "La contraseña original debe validar contra su hash");
	}

}
