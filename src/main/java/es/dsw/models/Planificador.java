package es.dsw.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.dsw.connections.MySqlConnection;

public class Planificador {
    private int idPlanificador;
    private int idUsuario;
    private String fecha;
    private int idReceta;
    private Recetas receta;
    
    
    private Users usuario;  // Usuario asignado
    private final MySqlConnection objMySqlConnection;
    private String usuarios; // Aquí almacenaremos los usuarios concatenados

    // Getters y Setters
    
    public void setUsuarios(String usuarios) {
        this.usuarios = usuarios;
    }
    public String getUsuarios() {
        return usuarios;
    }
    
    // Getters y Setters
      
    

    public Planificador() {
        objMySqlConnection = new MySqlConnection();
    }

    // Getters y Setters

    public Recetas getReceta() {
        return receta;
    }

    public void setReceta(Recetas receta) {
        this.receta = receta;
    }

    public Users getUsuario() {
        return usuario;
    }

    public void setUsuario(Users usuario) {
        this.usuario = usuario;
    }
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


public List<Planificador> obtenerRecetasPorUsuarios() {
    List<Planificador> recetasAsignadas = new ArrayList<>();
    objMySqlConnection.open();

    String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, " +
                 "GROUP_CONCAT(CONCAT(u.id_usuario, ':', u.nombre) SEPARATOR ', ') AS usuarios " +
                 "FROM Planificador p " +
                 "JOIN Recetas r ON p.id_receta = r.id_receta " +
                 "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                 "GROUP BY r.id_receta;";

    try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Planificador planificador = new Planificador();
            Recetas receta = new Recetas();
            receta.setIdReceta(rs.getInt("id_receta"));
            receta.setNombreReceta(rs.getString("nombre_receta"));
            receta.setDescripcion(rs.getString("descripcion"));
            receta.setIngredientes(rs.getString("ingredientes"));
            receta.setInstrucciones(rs.getString("instrucciones"));
            receta.setImagen(rs.getString("imagen"));
            planificador.setReceta(receta);

            // Guardar los usuarios asignados a la receta
            String usuariosConcat = rs.getString("usuarios");
            planificador.setUsuarios(usuariosConcat);

            recetasAsignadas.add(planificador);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return recetasAsignadas;
}

public List<Users> getListaUsuarios() {
    List<Users> listaUsuarios = new ArrayList<>();
    if (usuarios != null && !usuarios.isEmpty()) {
        String[] usuariosArray = usuarios.split(", ");
        for (String usuarioData : usuariosArray) {
            String[] datos = usuarioData.split(":"); // Separar id y nombre
            if (datos.length == 2) {
                try {
                    Users user = new Users();
                    user.setIdUser(Integer.parseInt(datos[0].trim())); // id_usuario
                    user.setNombre(datos[1].trim()); // nombre
                    listaUsuarios.add(user);
                } catch (NumberFormatException e) {
                    System.err.println("Error al parsear ID de usuario: " + datos[0]);
                }
            }
        }
    }
    return listaUsuarios;
}



public boolean eliminarAsignacion(int idUsuario, int idReceta) {
    String sql = "DELETE FROM Planificador WHERE id_usuario = ? AND id_receta = ?";
    
    objMySqlConnection.open(); // Abrir la conexión
    
    if (!objMySqlConnection.isError()) {
        try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idReceta);

            int filasAfectadas = stmt.executeUpdate(); // Ejecutar la eliminación
            
            objMySqlConnection.close(); // Cerrar la conexión
            
            return filasAfectadas > 0; // Retorna true si eliminó al menos una fila
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    objMySqlConnection.close(); // Cerrar la conexión en caso de error
    return false;
}

    
    
    
    
}

    
    
    
    



