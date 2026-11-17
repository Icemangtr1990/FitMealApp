package es.dsw.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import es.dsw.TestDatabase;

class RecetasIntegracionTest {

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
		List<ItemCompra> items = new Recetas().obtenerListaDeCompras(1, null, null);

		assertEquals(3, items.size(), "Debe agrupar 7 ingredientes en 3 entradas");
		assertEquals("100 g", buscar(items, "queso parmesano").getCantidadTotal());
		assertEquals("5 unidades", buscar(items, "huevos").getCantidadTotal());
		assertEquals("100 ml", buscar(items, "aceite de oliva").getCantidadTotal());
		assertTrue(buscar(items, "queso parmesano").getRecetasTexto().contains("Ensalada Cesar"));
	}

	@Test
	void filtraPorRangoDeFechas() {
		List<ItemCompra> items = new Recetas().obtenerListaDeCompras(1, "2026-11-10", "2026-11-10");

		assertEquals(2, items.size(), "Solo debe incluir la receta del día 10");
		assertEquals("50 g", buscar(items, "queso parmesano").getCantidadTotal());
		assertEquals("2 unidades", buscar(items, "huevos").getCantidadTotal());
		assertNull(buscar(items, "aceite de oliva"), "El aceite es de la receta del día 20");
	}

	@Test
	void devuelveListaVaciaSiNoHayAsignaciones() {
		List<ItemCompra> items = new Recetas().obtenerListaDeCompras(2, null, null);
		assertNotNull(items);
		assertEquals(0, items.size());
	}

}
