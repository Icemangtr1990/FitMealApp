package es.dsw.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.dsw.connections.MySqlConnection;

public class Recetas {
    
    // Atributos de la receta
    private int idReceta;
    private String nombreReceta;
    private String descripcion;
    private String instrucciones;
    private String ingredientes;  // Utilizando JSON como String
    private String imagen;
    private int idUsuario;  // Relación con la tabla usuarios
    private Date fecha;
    

    
    private final MySqlConnection objMySqlConnection;
    
    public Recetas() {
        objMySqlConnection = new MySqlConnection();
    }

    // Métodos Getter y Setter

    
    
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFechaFormateada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fecha);
    }

   
    

    public boolean insertReceta() {
    String sql = "INSERT INTO recetas (nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario) " +
                 "VALUES (?, ?, ?, ?, ?, ?)";
    
    objMySqlConnection.open();  // Abrir la conexión a la base de datos
    
    if (!objMySqlConnection.isError()) {
        try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
            // Establecer los parámetros de forma segura con PreparedStatement
            stmt.setString(1, this.nombreReceta);
            stmt.setString(2, this.descripcion);
            stmt.setString(3, this.instrucciones);
            stmt.setString(4, this.ingredientes);  // Aquí insertamos el JSON
            stmt.setString(5, this.imagen);
            stmt.setInt(6, this.idUsuario);
            
            stmt.executeUpdate();  // Ejecutar la inserción
            return true;  // Si la inserción fue exitosa
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    objMySqlConnection.close();  // Cerrar la conexión
    return false;  // Retornar falso si hubo algún error
}


    public List<Recetas> obtenerTodasLasRecetas() {
    List<Recetas> listaRecetas = new ArrayList<>();
    String sql = "SELECT * FROM recetas"; // Consulta SQL

    objMySqlConnection.open(); // Abrimos la conexión

    if (!objMySqlConnection.isError()) {
        ResultSet rs = objMySqlConnection.executeSelect(sql); // Ejecutamos la consulta

        try {
            while (rs.next()) {
                // Creamos un objeto Recetas y llenamos sus atributos
                Recetas receta = new Recetas();
                receta.setIdReceta(rs.getInt("id_receta"));
                receta.setNombreReceta(rs.getString("nombre_receta"));
                receta.setDescripcion(rs.getString("descripcion"));
                receta.setInstrucciones(rs.getString("instrucciones"));
                receta.setIngredientes(rs.getString("ingredientes"));
                receta.setImagen(rs.getString("imagen"));
                receta.setIdUsuario(rs.getInt("id_usuario"));
                String ingredientesFormateados = receta.getIngredientesFormateados();
                    receta.setIngredientes(ingredientesFormateados);

                // Añadimos la receta a la lista
                listaRecetas.add(receta);
            }
        } catch (SQLException e) {
            System.out.println("Error al procesar el ResultSet: " + e.getMessage());
        } finally {
            objMySqlConnection.close(); // Cerramos la conexión
        }
    } else {
        System.out.println("Error en la conexión: " + objMySqlConnection.msgError());
        objMySqlConnection.close(); // Cerramos la conexión en caso de error
    }

    return listaRecetas; // Devolvemos la lista de recetas
}


public boolean eliminarReceta(int idReceta) {
    String sql = "DELETE FROM recetas WHERE id_receta = " + idReceta;

    objMySqlConnection.open();  // Abrir la conexión

    if (!objMySqlConnection.isError()) {
        objMySqlConnection.executeInsert(sql);  // Ejecutar eliminación
        objMySqlConnection.close();  // Cerrar la conexión
        return true;  // Éxito al eliminar
    } else {
        System.out.println("Error al eliminar receta: " + objMySqlConnection.msgError());
    }

    objMySqlConnection.close();  // Asegurar cierre de conexión
    return false;  // Fallo al eliminar
}

public boolean asignarRecetaAUsuario(int idReceta, int idUsuario, String fecha) {
    String sql = "INSERT INTO Planificador (id_usuario, fecha, id_receta) VALUES (" +
                 idUsuario + ", '" + fecha + "', " + idReceta + ");";

    objMySqlConnection.open();  // Abrir la conexión

    if (!objMySqlConnection.isError()) {
        
        objMySqlConnection.executeInsert(sql);  // Ejecutar la inserción
        objMySqlConnection.close();  // Cerrar la conexión
        return true;  // Éxito al asignar
    } else {
        System.out.println("Error al asignar receta: " + objMySqlConnection.msgError());
    }

    return false;  // Si hubo un error
}

public ArrayList<Recetas> obtenerListaDeCompras(int idUsuario) {
    String sql = "SELECT r.nombre_receta, r.ingredientes " +
                 "FROM Recetas r " +
                 "JOIN Planificador p ON r.id_receta = p.id_receta " +
                 "WHERE p.id_usuario = " + idUsuario;

    ArrayList<Recetas> listaCompras = new ArrayList<>();
    objMySqlConnection.open();

    if (!objMySqlConnection.isError()) {
        ResultSet rs = objMySqlConnection.executeSelect(sql);

        try {
            while (rs.next()) {
                Recetas receta = new Recetas();
                receta.setNombreReceta(rs.getString("nombre_receta"));
                receta.setIngredientes(rs.getString("ingredientes"));
                listaCompras.add(receta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            objMySqlConnection.close();
        }
    } else {
        System.out.println("Error al ejecutar la consulta: " + objMySqlConnection.msgError());
    }

    return listaCompras;
}

 public String getIngredientesFormateados() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parsear la cadena JSON a una lista de mapas
            List<Map<String, String>> ingredientesList = objectMapper.readValue(ingredientes, List.class);
            StringBuilder ingredientesFormateados = new StringBuilder();
            
            // Formatear la lista de ingredientes en un formato más natural
            for (Map<String, String> ingrediente : ingredientesList) {
                String cantidad = ingrediente.get("cantidad");
                String ingredienteNombre = ingrediente.get("ingrediente");
                ingredientesFormateados.append(cantidad).append(" de ").append(ingredienteNombre).append(", ");
            }
            
            // Eliminar la última coma y espacio
            if (ingredientesFormateados.length() > 0) {
                ingredientesFormateados.setLength(ingredientesFormateados.length() - 2);
            }

            return ingredientesFormateados.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    



    
}
