package es.dsw.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.dsw.models.Planificador;
import es.dsw.models.Recetas;

@Repository
public class PlanificadorRepository {

	private final JdbcTemplate jdbc;

	public PlanificadorRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/** Recetas con sus usuarios asignados (GROUP_CONCAT con separadores ';;' y '|'). */
	public List<Planificador> findRecetasConUsuarios() {
		String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, "
				+ "r.instrucciones, r.imagen, "
				+ "GROUP_CONCAT(CONCAT(u.id_usuario, '|', u.nombre) SEPARATOR ';;') AS usuarios "
				+ "FROM Planificador p "
				+ "JOIN Recetas r ON p.id_receta = r.id_receta "
				+ "JOIN Usuarios u ON p.id_usuario = u.id_usuario "
				+ "GROUP BY r.id_receta";

		return jdbc.query(sql, (rs, rowNum) -> {
			Recetas receta = new Recetas();
			receta.setIdReceta(rs.getInt("id_receta"));
			receta.setNombreReceta(rs.getString("nombre_receta"));
			receta.setDescripcion(rs.getString("descripcion"));
			receta.setIngredientes(rs.getString("ingredientes"));
			receta.setInstrucciones(rs.getString("instrucciones"));
			receta.setImagen(rs.getString("imagen"));

			Planificador planificador = new Planificador();
			planificador.setReceta(receta);
			planificador.setUsuarios(rs.getString("usuarios"));
			return planificador;
		});
	}

	public boolean deleteAsignacion(int idUsuario, int idReceta) {
		return jdbc.update("DELETE FROM Planificador WHERE id_usuario = ? AND id_receta = ?",
				idUsuario, idReceta) > 0;
	}

}
