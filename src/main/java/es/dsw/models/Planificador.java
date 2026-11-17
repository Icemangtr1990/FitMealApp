package es.dsw.models;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Entidad de asignación receta-usuario. El acceso a datos está en PlanificadorRepository. */
public class Planificador {

	private static final Logger log = LoggerFactory.getLogger(Planificador.class);

	private int idPlanificador;
	private int idUsuario;
	private String fecha;
	private int idReceta;
	private Recetas receta;
	private Users usuario;
	private String usuarios;  // Usuarios concatenados por GROUP_CONCAT

	public Recetas getReceta() { return receta; }
	public void setReceta(Recetas receta) { this.receta = receta; }

	public Users getUsuario() { return usuario; }
	public void setUsuario(Users usuario) { this.usuario = usuario; }

	public int getIdPlanificador() { return idPlanificador; }
	public void setIdPlanificador(int idPlanificador) { this.idPlanificador = idPlanificador; }

	public int getIdUsuario() { return idUsuario; }
	public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

	public String getFecha() { return fecha; }
	public void setFecha(String fecha) { this.fecha = fecha; }

	public int getIdReceta() { return idReceta; }
	public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

	public void setUsuarios(String usuarios) { this.usuarios = usuarios; }
	public String getUsuarios() { return usuarios; }

	/** Convierte la cadena GROUP_CONCAT en una lista de usuarios. */
	public List<Users> getListaUsuarios() {
		List<Users> listaUsuarios = new ArrayList<>();
		if (usuarios != null && !usuarios.isEmpty()) {
			String[] usuariosArray = usuarios.split(";;");
			for (String usuarioData : usuariosArray) {
				String[] datos = usuarioData.split("\\|", 2);
				if (datos.length == 2) {
					try {
						Users user = new Users();
						user.setIdUser(Integer.parseInt(datos[0].trim()));
						user.setNombre(datos[1].trim());
						listaUsuarios.add(user);
					} catch (NumberFormatException e) {
						log.warn("Error al parsear ID de usuario: {}", datos[0]);
					}
				}
			}
		}
		return listaUsuarios;
	}

}
