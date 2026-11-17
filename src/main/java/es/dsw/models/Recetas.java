package es.dsw.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/** Entidad receta. El acceso a datos está en RecetasRepository. */
public class Recetas {

	private int idReceta;
	private String nombreReceta;
	private String descripcion;
	private String instrucciones;
	private String ingredientes;  // JSON en texto
	private String imagen;
	private int idUsuario;
	private Date fecha;

	public int getIdReceta() { return idReceta; }
	public void setIdReceta(int idReceta) { this.idReceta = idReceta; }

	public String getNombreReceta() { return nombreReceta; }
	public void setNombreReceta(String nombreReceta) { this.nombreReceta = nombreReceta; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

	public String getInstrucciones() { return instrucciones; }
	public void setInstrucciones(String instrucciones) { this.instrucciones = instrucciones; }

	public String getIngredientes() { return ingredientes; }
	public void setIngredientes(String ingredientes) { this.ingredientes = ingredientes; }

	public String getImagen() { return imagen; }
	public void setImagen(String imagen) { this.imagen = imagen; }

	public int getIdUsuario() { return idUsuario; }
	public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

	public Date getFecha() { return fecha; }
	public void setFecha(Date fecha) { this.fecha = fecha; }

	public String getFechaFormateada() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return fecha == null ? "" : sdf.format(fecha);
	}

	/** Convierte el JSON de ingredientes en texto legible. */
	public String getIngredientesFormateados() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<Map<String, String>> ingredientesList = objectMapper.readValue(ingredientes, List.class);
			StringBuilder resultado = new StringBuilder();

			for (Map<String, String> ingrediente : ingredientesList) {
				resultado.append(ingrediente.get("cantidad"))
						.append(" de ")
						.append(ingrediente.get("ingrediente"))
						.append(", ");
			}

			if (resultado.length() > 0) {
				resultado.setLength(resultado.length() - 2);
			}
			return resultado.toString();
		} catch (Exception e) {
			return "";
		}
	}

}
