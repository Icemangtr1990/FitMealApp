package es.dsw.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ItemCompraTest {

	@Test
	void primeraLetraEnMayuscula() {
		assertEquals("Queso parmesano", new ItemCompra("queso parmesano").getIngrediente());
	}

	@Test
	void sumaCantidadesConLaMismaUnidad() {
		ItemCompra item = new ItemCompra("queso parmesano");
		item.anadir("200g", "Ensalada César");
		item.anadir("150g", "Pasta Carbonara");

		assertEquals("350 g", item.getCantidadTotal());
	}

	@Test
	void pluralizaLaUnidadAlSumar() {
		ItemCompra item = new ItemCompra("huevos");
		item.anadir("2 unidades", "Carbonara");
		item.anadir("3 unidades", "Tortilla");

		assertEquals("5 unidades", item.getCantidadTotal());
	}

	@Test
	void enumeraCantidadesConUnidadesDistintas() {
		ItemCompra item = new ItemCompra("ingrediente variado");
		item.anadir("200ml", "Sopa");
		item.anadir("1 cucharada", "Curry");

		assertEquals("200ml + 1 cucharada", item.getCantidadTotal());
	}

	@Test
	void muestraGuionSiNoHayCantidades() {
		assertEquals("—", new ItemCompra("sin cantidad").getCantidadTotal());
	}

}
