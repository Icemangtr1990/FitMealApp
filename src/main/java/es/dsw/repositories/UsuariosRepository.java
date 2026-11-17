package es.dsw.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.dsw.models.Users;

@Repository
public class UsuariosRepository {

	private static final String SELECT = "SELECT id_usuario, nombre, password, email, rol FROM usuarios";

	private final JdbcTemplate jdbc;

	public UsuariosRepository(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Users> findAll() {
		return jdbc.query(SELECT, (rs, rowNum) -> map(rs));
	}

	public Users findByUsername(String username) {
		List<Users> usuarios = jdbc.query(SELECT + " WHERE nombre = ?", (rs, rowNum) -> map(rs), username);
		return usuarios.isEmpty() ? null : usuarios.get(0);
	}

	public boolean insert(Users usuario) {
		return jdbc.update("INSERT INTO usuarios (nombre, password, email, rol) VALUES (?, ?, ?, ?)",
				usuario.getNombre(), usuario.getPasswd(), usuario.getEmail(), usuario.getUserRole()) > 0;
	}

	public boolean deleteById(int idUsuario) {
		return jdbc.update("DELETE FROM usuarios WHERE id_usuario = ?", idUsuario) > 0;
	}

	public void updatePassword(int idUsuario, String hash) {
		jdbc.update("UPDATE usuarios SET password = ? WHERE id_usuario = ?", hash, idUsuario);
	}

	private Users map(ResultSet rs) throws SQLException {
		Users usuario = new Users();
		usuario.setIdUser(rs.getInt("id_usuario"));
		usuario.setNombre(rs.getString("nombre"));
		usuario.setPasswd(rs.getString("password"));
		usuario.setEmail(rs.getString("email"));
		usuario.setUserRole(rs.getString("rol"));
		return usuario;
	}

}
