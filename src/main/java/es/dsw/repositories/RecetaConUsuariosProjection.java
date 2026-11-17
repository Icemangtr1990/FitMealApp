package es.dsw.repositories;

/** Proyección de una receta con sus usuarios agrupados (GROUP_CONCAT). */
public interface RecetaConUsuariosProjection {

	Integer getIdReceta();
	String getNombreReceta();
	String getDescripcion();
	String getIngredientes();
	String getInstrucciones();
	String getImagen();
	String getUsuarios();

}
