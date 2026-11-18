package es.dsw.app;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import es.dsw.TestDatabase;

@SpringBootTest(classes = FitMealAppApplication.class)
@AutoConfigureMockMvc
class WebSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() throws Exception {
		TestDatabase.reset();
	}

	private MockHttpSession login(String usuario) throws Exception {
		MvcResult resultado = mockMvc.perform(formLogin("/logginprocess")
				.user(usuario).password("1234"))
				.andExpect(authenticated())
				.andReturn();
		return (MockHttpSession) resultado.getRequest().getSession(false);
	}

	@Test
	void portadaAnonimaNoMuestraAnonymousUser() throws Exception {
		mockMvc.perform(get("/index"))
				.andExpect(status().isOk())
				.andExpect(content().string(not(containsString("anonymousUser"))));
	}

	@Test
	void recetasRequiereAutenticacion() throws Exception {
		// Con httpBasic activo, Spring responde 401 en vez de redirigir al login.
		mockMvc.perform(get("/recetas"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminPuedeEntrarYVerRecetas() throws Exception {
		MockHttpSession session = login("admin");
		mockMvc.perform(get("/recetas").session(session))
				.andExpect(status().isOk());
	}

	@Test
	void usuarioNormalNoPuedeAsignarRecetas() throws Exception {
		MockHttpSession session = login("pepe");
		mockMvc.perform(post("/asignarReceta").session(session).with(csrf())
				.param("idReceta", "1")
				.param("idUsuario", "2")
				.param("fecha", "2026-11-20"))
				.andExpect(status().isForbidden());
	}

	@Test
	void usuarioNormalNoPuedeCrearUsuarios() throws Exception {
		MockHttpSession session = login("pepe");
		mockMvc.perform(post("/admin/crear-usuario").session(session).with(csrf())
				.param("nombre", "nuevo")
				.param("passwd", "x")
				.param("email", "nuevo@test.es")
				.param("userRole", "usuario"))
				.andExpect(status().isForbidden());
	}

}
