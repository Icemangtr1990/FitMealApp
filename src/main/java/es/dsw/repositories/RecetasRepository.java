package es.dsw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.dsw.models.Recetas;

@Repository
public class RecetasRepository {

	private final JdbcTemplate jdbc;

	public RecetasRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Recetas> findAll() {
		return jdbc.query("SELECT id_receta, nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario FROM recetas",
				(rs, rowNum) -> map(rs));
	}

	public Recetas findById(int idReceta) {
		return jdbc.query("SELECT id_receta, nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario FROM recetas WHERE id_receta = ?",
				(rs, rowNum) -> map(rs), idReceta).stream().findFirst().orElse(null);
	}

	public boolean insert(Recetas receta) {
		return jdbc.update("INSERT INTO recetas (nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario) "
				+ "VALUES (?, ?, ?, ?, ?, ?)",
				receta.getNombreReceta(), receta.getDescripcion(), receta.getInstrucciones(),
				receta.getIngredientes(), receta.getImagen(), receta.getIdUsuario()) > 0;
	}

	public boolean deleteById(int idReceta) {
		return jdbc.update("DELETE FROM recetas WHERE id_receta = ?", idReceta) > 0;
	}

	public boolean assign(int idReceta, int idUsuario, String fecha) {
		return jdbc.update("INSERT INTO Planificador (id_usuario, fecha, id_receta) VALUES (?, ?, ?)",
				idUsuario, fecha, idReceta) > 0;
	}

	/** Recetas asignadas a un usuario, con la fecha de asignación. */
	public List<Recetas> findByUsuario(int idUsuario) {
		return jdbc.query("SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, "
				+ "r.instrucciones, r.imagen, p.fecha, p.id_usuario "
				+ "FROM Planificador p JOIN Recetas r ON p.id_receta = r.id_receta "
				+ "WHERE p.id_usuario = ?",
				(rs, rowNum) -> {
					Recetas receta = map(rs);
					receta.setFecha(rs.getDate("fecha"));
					return receta;
				}, idUsuario);
	}

	/** Filas (receta + ingredientes) usadas para montar la lista de la compra. */
	public List<Recetas> findShoppingRows(int idUsuario, String fechaDesde, String fechaHasta) {
		StringBuilder sql = new StringBuilder("SELECT r.nombre_receta, r.ingredientes "
				+ "FROM Recetas r JOIN Planificador p ON r.id_receta = p.id_receta "
				+ "WHERE p.id_usuario = ?");
		List<Object> parametros = new ArrayList<>();
		parametros.add(idUsuario);

		if (fechaDesde != null && !fechaDesde.isBlank()) {
			sql.append(" AND p.fecha >= ?");
			parametros.add(fechaDesde);
		}
		if (fechaHasta != null && !fechaHasta.isBlank()) {
			sql.append(" AND p.fecha <= ?");
			parametros.add(fechaHasta);
		}
		sql.append(" ORDER BY r.nombre_receta");

		return jdbc.query(sql.toString(), (rs, rowNum) -> {
			Recetas receta = new Recetas();
			receta.setNombreReceta(rs.getString("nombre_receta"));
			receta.setIngredientes(rs.getString("ingredientes"));
			return receta;
		}, parametros.toArray());
	}

	private Recetas map(ResultSet rs) throws SQLException {
		Recetas receta = new Recetas();
		receta.setIdReceta(rs.getInt("id_receta"));
		receta.setNombreReceta(rs.getString("nombre_receta"));
		receta.setDescripcion(rs.getString("descripcion"));
		receta.setInstrucciones(rs.getString("instrucciones"));
		receta.setIngredientes(rs.getString("ingredientes"));
		receta.setImagen(rs.getString("imagen"));
		receta.setIdUsuario(rs.getInt("id_usuario"));
		return receta;
	}

}
