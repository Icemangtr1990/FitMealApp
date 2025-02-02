package es.dsw.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import es.dsw.connections.MySqlConnection;

public class Planificador {
    private int idPlanificador;
    private int idUsuario;
    private String fecha;
    private int idReceta;

    private final MySqlConnection objMySqlConnection;

    public Planificador() {
        objMySqlConnection = new MySqlConnection();
    }

    // Getters y Setters
    public int getIdPlanificador() { return idPlanificador; }
    public void setIdPlanificador(int idPlanificador) { this.idPlanificador = idPlanificador; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getIdReceta() { return idReceta; }
    public void setIdReceta(int idReceta) { this.idReceta = idReceta; }


    // Método para obtener las recetas asignadas a un usuario
    public ArrayList<Recetas> obtenerRecetasPorUsuario(int idUsuario) {
        // Consulta para obtener las recetas asignadas al usuario, incluyendo la fecha
        String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, p.fecha " +
                     "FROM Planificador p " +
                     "JOIN Recetas r ON p.id_receta = r.id_receta " +
                     "WHERE p.id_usuario = ?";
        
        ArrayList<Recetas> listaRecetas = new ArrayList<>();
        objMySqlConnection.open();
        
        if (objMySqlConnection.isError()) {
            System.out.println("Error en la conexión: " + objMySqlConnection.msgError());
            return listaRecetas;  // Salir si hay un error
        }
    
        try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);  // Establecer el idUsuario de manera segura
            ResultSet rs = stmt.executeQuery();
    
            if (!rs.next()) {
                System.out.println("No se encontraron recetas para el usuario con id: " + idUsuario);
            } else {
                do {
                    Recetas receta = new Recetas();
                    receta.setIdReceta(rs.getInt("id_receta"));
                    receta.setNombreReceta(rs.getString("nombre_receta"));
                    receta.setDescripcion(rs.getString("descripcion"));
                    receta.setIngredientes(rs.getString("ingredientes"));
                    receta.setInstrucciones(rs.getString("instrucciones"));
                    receta.setImagen(rs.getString("imagen"));
                    receta.setFecha(rs.getDate("fecha"));  // Establecer la fecha
                    String ingredientesFormateados = receta.getIngredientesFormateados();
                    receta.setIngredientes(ingredientesFormateados);
    
                    listaRecetas.add(receta);
                } while (rs.next());
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            objMySqlConnection.close();  // Cerrar la conexión
        }
    
        return listaRecetas;
    }
    
    
    
    

}

