package es.dsw.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

/**
 * Centraliza el tratamiento de errores de los controladores MVC y evita que
 * se muestren trazas o mensajes técnicos al visitante.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ConstraintViolationException.class)
	public String handleValidation(ConstraintViolationException ex, Model model) {
		log.warn("Validación fallida: {}", ex.getMessage());
		model.addAttribute("mensaje", "Revisa los campos del formulario: no pueden estar vacíos y el email debe ser válido.");
		return "error";
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public String handleIntegrity(DataIntegrityViolationException ex, Model model) {
		log.warn("Conflicto de integridad en BD: {}", ex.getMessage());
		model.addAttribute("mensaje", "Ya existe un usuario con ese nombre o email.");
		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String handleGeneral(Exception ex, Model model) {
		log.error("Error no esperado en la aplicación", ex);
		model.addAttribute("mensaje", "Se ha producido un error inesperado. Inténtalo de nuevo.");
		return "error";
	}

}
