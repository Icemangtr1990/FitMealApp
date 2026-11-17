package es.dsw.repositories;

import java.sql.Date;

/** Proyección de una fila planificador + receta (consulta nativa). */
public interface PlanificadorRecetaProjection {

	Integer getIdReceta();
	String getNombreReceta();
	String getDescripcion();
	String getIngredientes();
	String getInstrucciones();
	String getImagen();
	Date getFecha();

}
