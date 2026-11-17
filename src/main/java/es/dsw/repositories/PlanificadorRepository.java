package es.dsw.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.dsw.models.Planificador;

public interface PlanificadorRepository extends JpaRepository<Planificador, Integer> {

	long deleteByIdUsuarioAndIdReceta(int idUsuario, int idReceta);

	@Query(value = "SELECT r.id_receta AS idReceta, r.nombre_receta AS nombreReceta, "
			+ "r.descripcion AS descripcion, r.ingredientes AS ingredientes, "
			+ "r.instrucciones AS instrucciones, r.imagen AS imagen, "
			+ "GROUP_CONCAT(CONCAT(u.id_usuario, '|', u.nombre) SEPARATOR ';;') AS usuarios "
			+ "FROM planificador p "
			+ "JOIN recetas r ON p.id_receta = r.id_receta "
			+ "JOIN usuarios u ON p.id_usuario = u.id_usuario "
			+ "GROUP BY r.id_receta", nativeQuery = true)
	List<RecetaConUsuariosProjection> findRecetasConUsuarios();

}
