package es.dsw.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

   
    public boolean insertReceta() {
        String sql = "INSERT INTO recetas (nombre_receta, descripcion, instrucciones, ingredientes, imagen, id_usuario) " +
                     "VALUES ('" + this.nombreReceta + "', " +
                     "'" + this.descripcion + "', " +
                     "'" + this.instrucciones + "', " +
                     "'" + this.ingredientes + "', " + 
                     "'" + this.imagen + "', " +
                     this.idUsuario + ");";
        
        objMySqlConnection.open();  // Abrir la conexión a la base de datos
        if (!objMySqlConnection.isError()) {
            System.out.println(sql);
            objMySqlConnection.executeInsert(sql);  // Ejecutar la inserción
            return true;  // Si la inserción fue exitosa
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

    
}
