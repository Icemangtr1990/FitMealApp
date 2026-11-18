package es.dsw.dtos;

import java.time.Instant;
import java.util.Map;

/** Respuesta de error uniforme para la API REST. */
public record ApiError(
		int status,
		String error,
		String mensaje,
		Map<String, String> detalles,
		Instant timestamp) {

	public static ApiError of(int status, String error, String mensaje) {
		return new ApiError(status, error, mensaje, Map.of(), Instant.now());
	}

	public static ApiError of(int status, String error, String mensaje, Map<String, String> detalles) {
		return new ApiError(status, error, mensaje, detalles, Instant.now());
	}

}
