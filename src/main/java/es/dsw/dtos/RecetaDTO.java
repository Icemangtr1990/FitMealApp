package es.dsw.dtos;

import java.util.List;

/** DTO de receta para la API REST (no expone la entidad directamente). */
public record RecetaDTO(
		int id,
		String nombre,
		String descripcion,
		String instrucciones,
		List<IngredienteDTO> ingredientes,
		String imagen,
		String fecha) {
}
