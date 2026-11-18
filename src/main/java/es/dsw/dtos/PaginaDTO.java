package es.dsw.dtos;

import java.util.List;

/** Respuesta paginada para la API REST. */
public record PaginaDTO<T>(
		List<T> contenido,
		int pagina,
		int tamano,
		long totalElementos,
		int totalPaginas) {
}
