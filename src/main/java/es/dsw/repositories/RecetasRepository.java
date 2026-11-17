package es.dsw.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.dsw.models.Recetas;

public interface RecetasRepository extends JpaRepository<Recetas, Integer> {

	default boolean insert(Recetas receta) {
		return save(receta) != null;
	}

	@Query(value = "SELECT r.id_receta AS idReceta, r.nombre_receta AS nombreReceta, "
			+ "r.descripcion AS descripcion, r.ingredientes AS ingredientes, "
			+ "r.instrucciones AS instrucciones, r.imagen AS imagen, p.fecha AS fecha "
			+ "FROM planificador p JOIN recetas r ON p.id_receta = r.id_receta "
			+ "WHERE p.id_usuario = :idUsuario ORDER BY p.fecha", nativeQuery = true)
	List<PlanificadorRecetaProjection> findRecetasByUsuario(@Param("idUsuario") int idUsuario);

	@Query(value = "SELECT r.id_receta AS idReceta, r.nombre_receta AS nombreReceta, "
			+ "r.descripcion AS descripcion, r.ingredientes AS ingredientes, "
			+ "r.instrucciones AS instrucciones, r.imagen AS imagen, p.fecha AS fecha "
			+ "FROM planificador p JOIN recetas r ON p.id_receta = r.id_receta "
			+ "WHERE p.id_usuario = :idUsuario "
			+ "AND (:desde IS NULL OR p.fecha >= :desde) "
			+ "AND (:hasta IS NULL OR p.fecha <= :hasta) "
			+ "ORDER BY r.nombre_receta", nativeQuery = true)
	List<PlanificadorRecetaProjection> findShoppingRows(
			@Param("idUsuario") int idUsuario,
			@Param("desde") LocalDate desde,
			@Param("hasta") LocalDate hasta);

}
