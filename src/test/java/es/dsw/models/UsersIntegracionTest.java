package es.dsw.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.dsw.TestDatabase;

class UsersIntegracionTest {

	@BeforeEach
	void setUp() throws Exception {
		TestDatabase.reset();
	}

	@Test
	void insertaYEncuentraUsuarioConApostrofo() {
		Users usuario = new Users();
		usuario.setNombre("O'Brien");
		usuario.setEmail("obrien@test.es");
		usuario.setPasswd("hash_seguro");
		usuario.setUserRole("usuario");

		assertTrue(usuario.insertUser(), "El insert debe funcionar con apóstrofos (PreparedStatement)");

		Users encontrado = new Users().getUserByUsername("O'Brien");
		assertNotNull(encontrado);
		assertEquals("obrien@test.es", encontrado.getEmail());
		assertEquals("usuario", encontrado.getUserRole());
	}

	@Test
	void listaUsuariosDeLaBaseDeDatos() {
		assertEquals(2, new Users().getUser().size());
	}

}
