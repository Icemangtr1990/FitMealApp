package es.dsw.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.dsw.TestDatabase;
import es.dsw.app.FitMealAppApplication;
import es.dsw.services.UsuariosService;

@SpringBootTest(classes = FitMealAppApplication.class)
class UsersIntegracionTest {

	@Autowired
	private UsuariosService usuariosService;

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

		assertTrue(usuariosService.crear(usuario), "El insert debe funcionar con apóstrofos (PreparedStatement)");

		Users encontrado = usuariosService.findByUsername("O'Brien");
		assertNotNull(encontrado);
		assertEquals("obrien@test.es", encontrado.getEmail());
		assertEquals("usuario", encontrado.getUserRole());
	}

	@Test
	void listaUsuariosDeLaBaseDeDatos() {
		assertEquals(2, usuariosService.findAll().size());
	}

}
