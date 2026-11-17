package es.dsw.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import es.dsw.TestDatabase;
import es.dsw.app.FitMealAppApplication;
import es.dsw.services.RecetasService;

@SpringBootTest(classes = FitMealAppApplication.class)
class RecetasIntegracionTest {

	@Autowired
	private RecetasService recetasService;

	@BeforeEach
	void setUp() throws Exception {
		TestDatabase.reset();
	}

	private ItemCompra buscar(List<ItemCompra> items, String nombre) {
		return items.stream()
				.filter(i -> i.getIngrediente().equalsIgnoreCase(nombre))
				.findFirst()
				.orElse(null);
	}

	@Test
	void agrupaIngredientesRepetidosSumandoCantidades() {
		List<ItemCompra> items = recetasService.listaDeCompras(1, null, null);

		assertEquals(3, items.size(), "Debe agrupar 7 ingredientes en 3 entradas");
		assertEquals("100 g", buscar(items, "queso parmesano").getCantidadTotal());
		assertEquals("5 unidades", buscar(items, "huevos").getCantidadTotal());
		assertEquals("100 ml", buscar(items, "aceite de oliva").getCantidadTotal());
		assertTrue(buscar(items, "queso parmesano").getRecetasTexto().contains("Ensalada Cesar"));
	}

	@Test
	void filtraPorRangoDeFechas() {
		List<ItemCompra> items = recetasService.listaDeCompras(1, "2026-11-10", "2026-11-10");

		assertEquals(2, items.size(), "Solo debe incluir la receta del día 10");
		assertEquals("50 g", buscar(items, "queso parmesano").getCantidadTotal());
		assertEquals("2 unidades", buscar(items, "huevos").getCantidadTotal());
		assertNull(buscar(items, "aceite de oliva"), "El aceite es de la receta del día 20");
	}

	@Test
	void devuelveListaVaciaSiNoHayAsignaciones() {
		List<ItemCompra> items = recetasService.listaDeCompras(2, null, null);
		assertEquals(0, items.size());
	}

}
