package es.dsw.models;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Un ingrediente de la lista de la compra, con todas las cantidades y recetas
 * en las que aparece. Permite sumar cantidades cuando tienen la misma unidad
 * (por ejemplo, 200g + 150g = 350g).
 */
public class ItemCompra {

	private static final Pattern CANTIDAD = Pattern.compile("^([0-9]+(?:[.,][0-9]+)?)\\s*(.*)$");

	private final String ingrediente;
	private final List<String> cantidades = new ArrayList<>();
	private final List<String> recetas = new ArrayList<>();

	public ItemCompra(String ingrediente) {
		this.ingrediente = ingrediente;
	}

	public String getIngrediente() {
		return ingrediente;
	}

	public List<String> getCantidades() {
		return cantidades;
	}

	public List<String> getRecetas() {
		return recetas;
	}

	public void anadir(String cantidad, String receta) {
		cantidades.add((cantidad == null || cantidad.isBlank()) ? "—" : cantidad.trim());
		if (receta != null && !receta.isBlank() && !recetas.contains(receta.trim())) {
			recetas.add(receta.trim());
		}
	}

	public String getRecetasTexto() {
		return String.join(", ", recetas);
	}

	/**
	 * Suma las cantidades si todas comparten unidad; si no, las enumera con " + ".
	 */
	public String getCantidadTotal() {
		if (cantidades.isEmpty()) {
			return "—";
		}

		double suma = 0;
		String unidadNormalizada = null;

		for (String cantidad : cantidades) {
			Parsed parsed = parsear(cantidad);
			if (parsed == null) {
				return String.join(" + ", cantidades);
			}
			String actual = normalizar(parsed.unidad);
			if (unidadNormalizada == null) {
				unidadNormalizada = actual;
			} else if (!unidadNormalizada.equals(actual)) {
				return String.join(" + ", cantidades);
			}
			suma += parsed.numero;
		}

		String texto = (suma % 1 == 0) ? String.valueOf((long) suma) : String.valueOf(suma);
		String unidad = parsear(cantidades.get(0)).unidad;
		if (suma != 1 && unidad.endsWith("unidad")) {
			unidad += "es";
		}
		return unidad.isEmpty() ? texto : texto + " " + unidad;
	}

	private static Parsed parsear(String texto) {
		Matcher matcher = CANTIDAD.matcher(texto.trim());
		if (!matcher.matches()) {
			return null;
		}
		try {
			double numero = Double.parseDouble(matcher.group(1).replace(',', '.'));
			return new Parsed(numero, matcher.group(2).trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static String normalizar(String unidad) {
		String resultado = unidad.toLowerCase().trim();
		if (resultado.endsWith("s")) {
			resultado = resultado.substring(0, resultado.length() - 1);
		}
		return resultado;
	}

	private static class Parsed {
		private final double numero;
		private final String unidad;

		Parsed(double numero, String unidad) {
			this.numero = numero;
			this.unidad = unidad;
		}
	}

}
