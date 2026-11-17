package es.dsw.dtos;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/** Cuerpo para crear recetas desde la API REST. */
public record RecetaRequest(
		@NotBlank String nombre,
		@NotBlank String descripcion,
		@NotBlank String instrucciones,
		@NotEmpty @Valid List<IngredienteDTO> ingredientes,
		String imagen) {
}
