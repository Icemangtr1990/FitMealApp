package es.dsw.app;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import es.dsw.controllers.ApiController;
import es.dsw.dtos.ApiError;
import jakarta.validation.ConstraintViolationException;

/**
 * Manejo de errores exclusivo para la API REST: devuelve JSON con códigos HTTP
 * correctos en lugar de la vista HTML de las páginas MVC.
 */
@RestControllerAdvice(assignableTypes = ApiController.class)
public class ApiExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleBodyValidation(MethodArgumentNotValidException ex) {
		Map<String, String> detalles = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			detalles.put(error.getField(), error.getDefaultMessage());
		}
		log.warn("Validación de API fallida: {}", detalles);
		return ResponseEntity.badRequest()
				.body(ApiError.of(400, "Validación incorrecta", "Revisa los campos enviados.", detalles));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleJson(HttpMessageNotReadableException ex) {
		log.warn("JSON no legible en API: {}", ex.getMessage());
		return ResponseEntity.badRequest()
				.body(ApiError.of(400, "JSON inválido", "Revisa el cuerpo de la petición."));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleParamValidation(ConstraintViolationException ex) {
		Map<String, String> detalles = new LinkedHashMap<>();
		ex.getConstraintViolations().forEach(v ->
				detalles.put(v.getPropertyPath().toString(), v.getMessage()));
		log.warn("Validación de parámetros de API fallida: {}", detalles);
		return ResponseEntity.badRequest()
				.body(ApiError.of(400, "Validación incorrecta", "Revisa los parámetros enviados.", detalles));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex) {
		log.warn("Conflicto de integridad en API: {}", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiError.of(409, "Conflicto de datos", "El recurso ya existe o vulnera una restricción."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneral(Exception ex) {
		log.error("Error no esperado en la API", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiError.of(500, "Error interno", "Se ha producido un error inesperado."));
	}

}
