package es.dsw.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/** Entidad de asignación receta-usuario. */
@Entity
@Table(name = "planificador")
public class Planificador {

	private static final Logger log = LoggerFactory.getLogger(Planificador.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_planificador")
	private int idPlanificador;

	@Column(name = "id_usuario")
	private int idUsuario;

	@Column(name = "fecha")
	private LocalDate fecha;

	@Column(name = "id_receta")
	private int idReceta;

	/** Campos usados solo en vistas (no se persisten). */
	@Transient
	private Recetas receta;

	@Transient
	private Users usuario;

	@Transient
	private String usuarios;

	public Planificador() {
	}

	public Recetas getReceta() { return receta; }
	public void setReceta(Recetas receta) { this.receta = receta; }

	public Users getUsuario() { return usuario; }
	public void setUsuario(Users usuario) { this.usuario = usuario; }

	public int getIdPlanificador() { return idPlanificador; }
	public void setIdPlanificador(int idPlanificador) { this.idPlanificador = idPlanificador; }

	public int getIdUsuario() { return idUsuario; }
	public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

	public LocalDate getFecha() { return fecha; }
	public void setFecha(LocalDate fecha) { this.fecha = fecha; }

	public int getIdReceta() { return idReceta; }
	public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

	public void setUsuarios(String usuarios) { this.usuarios = usuarios; }
	public String getUsuarios() { return usuarios; }

	/** Convierte la cadena GROUP_CONCAT en una lista de usuarios. */
	public List<Users> getListaUsuarios() {
		List<Users> listaUsuarios = new ArrayList<>();
		List<Integer> idsVistos = new ArrayList<>();
		if (usuarios != null && !usuarios.isEmpty()) {
			String[] usuariosArray = usuarios.split(";;");
			for (String usuarioData : usuariosArray) {
				String[] datos = usuarioData.split("\\|", 2);
				if (datos.length == 2) {
					try {
						int id = Integer.parseInt(datos[0].trim());
						if (!idsVistos.contains(id)) {
							idsVistos.add(id);
							Users user = new Users();
							user.setIdUser(id);
							user.setNombre(datos[1].trim());
							listaUsuarios.add(user);
						}
					} catch (NumberFormatException e) {
						log.warn("Error al parsear ID de usuario: {}", datos[0]);
					}
				}
			}
		}
		return listaUsuarios;
	}

}
