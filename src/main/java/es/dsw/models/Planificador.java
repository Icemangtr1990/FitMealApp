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
    //private String[] usuarios; // Ahora será un arreglo de usuarios (nombres)
    
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

//     public List<Planificador> obtenerRecetasPorUsuarios() {
//         List<Planificador> recetasAsignadas = new ArrayList<>();
        
//         // Abrir conexión
//         objMySqlConnection.open();
//         if (objMySqlConnection.isError()) {
//             System.out.println("Error en la conexión: " + objMySqlConnection.msgError());
//             return recetasAsignadas;  // Salir si hay un error
//         }
    
//         // Consulta para obtener todas las recetas asignadas a todos los usuarios
//         // String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, p.id_usuario, p.fecha " +
//         //              "FROM Planificador p " +
//         //              "JOIN Recetas r ON p.id_receta = r.id_receta";
//         String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, p.id_usuario, u.nombre " +
//              "FROM Planificador p " +
//              "JOIN Recetas r ON p.id_receta = r.id_receta " +
//              "JOIN Users u ON p.id_usuario = u.id_usuario"; // Asegúrate de que el nombre de la columna sea correcto

// // ... código para ejecutar la consulta y asignar el nombre al usuario


//         // Ejecutar la consulta y obtener los resultados
//         try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
//             ResultSet rs = stmt.executeQuery();
            
//             while (rs.next()) {
//                 // Crear un objeto Planificador para almacenar la receta asignada
//                 Planificador planificador = new Planificador();
//                 Recetas receta = new Recetas();
//                 receta.setIdReceta(rs.getInt("id_receta"));
//                 receta.setNombreReceta(rs.getString("nombre_receta"));
//                 receta.setDescripcion(rs.getString("descripcion"));
//                 receta.setIngredientes(rs.getString("ingredientes"));
//                 receta.setInstrucciones(rs.getString("instrucciones"));
//                 receta.setImagen(rs.getString("imagen"));
//                 planificador.setReceta(receta); // Asignar la receta al Planificador
                
//                 // Aquí obtienes el id_usuario directamente de la consulta
//                 Users usuario = new Users();
//                 usuario.setIdUser(rs.getInt("id_usuario"));
//                 planificador.setUsuario(usuario); // Asignar el usuario al Planificador
    
//                 planificador.setFecha(rs.getString("fecha")); // Establecer la fecha
    
//                 // Agregar el Planificador a la lista
//                 recetasAsignadas.add(planificador);
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         } finally {
//             // Cerrar la conexión después de usarla
//             objMySqlConnection.close();
//         }
    
//         return recetasAsignadas; // Retornar todas las recetas asignadas
//     }

// public List<Planificador> obtenerRecetasPorUsuarios() {
//     List<Planificador> recetasAsignadas = new ArrayList<>();
//     objMySqlConnection.open();
//     // Consulta para obtener todas las recetas asignadas a todos los usuarios
//     // Incluye el nombre del usuario
//     String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, p.id_usuario, u.nombre, p.fecha " +
//                  "FROM Planificador p " +
//                  "JOIN Recetas r ON p.id_receta = r.id_receta " +
//                  "JOIN Usuarios u ON p.id_usuario = u.id_usuario"; // Asegúrate de que el nombre de la columna sea correcto
    
//     // Ejecutar la consulta y obtener los resultados
//     try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
//         ResultSet rs = stmt.executeQuery();
        
//         while (rs.next()) {
//             // Crear un objeto Planificador para almacenar la receta asignada
//             Planificador planificador = new Planificador();
//             Recetas receta = new Recetas();
//             receta.setIdReceta(rs.getInt("id_receta"));
//             receta.setNombreReceta(rs.getString("nombre_receta"));
//             receta.setDescripcion(rs.getString("descripcion"));
//             receta.setIngredientes(rs.getString("ingredientes"));
//             receta.setInstrucciones(rs.getString("instrucciones"));
//             receta.setImagen(rs.getString("imagen"));
//             planificador.setReceta(receta); // Asignar la receta al Planificador
            
//             // Crear y asignar el usuario con su nombre
//             Users usuario = new Users();
//             usuario.setIdUser(rs.getInt("id_usuario"));
//             usuario.setNombre(rs.getString("nombre")); // Asignar el nombre del usuario
//             planificador.setUsuario(usuario); // Asignar el usuario al Planificador

//             planificador.setFecha(rs.getString("fecha")); // Establecer la fecha

//             // Agregar el Planificador a la lista
//             recetasAsignadas.add(planificador);
//         }
//     } catch (SQLException e) {
//         e.printStackTrace();
//     }
    
//     return recetasAsignadas; // Retornar todas las recetas asignadas
// }

// public List<Planificador> obtenerRecetasPorUsuarios() {
//     List<Planificador> recetasAsignadas = new ArrayList<>();
//     objMySqlConnection.open();

//     // Consulta para obtener todas las recetas con sus usuarios asignados
//     String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, " +
//                  "GROUP_CONCAT(u.nombre SEPARATOR ', ') AS usuarios " +
//                  "FROM Planificador p " +
//                  "JOIN Recetas r ON p.id_receta = r.id_receta " +
//                  "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
//                  "GROUP BY r.id_receta;";

//     try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
//         ResultSet rs = stmt.executeQuery();

//         while (rs.next()) {
//             // Crear un objeto Planificador para almacenar la receta asignada
//             Planificador planificador = new Planificador();
//             Recetas receta = new Recetas();
//             receta.setIdReceta(rs.getInt("id_receta"));
//             receta.setNombreReceta(rs.getString("nombre_receta"));
//             receta.setDescripcion(rs.getString("descripcion"));
//             receta.setIngredientes(rs.getString("ingredientes"));
//             receta.setInstrucciones(rs.getString("instrucciones"));
//             receta.setImagen(rs.getString("imagen"));
//             planificador.setReceta(receta); // Asignar la receta al Planificador

//             // Obtener los usuarios asignados a la receta
//             String usuarios = rs.getString("usuarios");
//             System.out.println(usuarios);
//             planificador.setUsuarios(usuarios); // Asignar los usuarios al Planificador

//             // Agregar el Planificador a la lista
//             recetasAsignadas.add(planificador);
//         }
//     } catch (SQLException e) {
//         e.printStackTrace();
//     }

//     return recetasAsignadas; // Retornar todas las recetas asignadas
// }

public List<Planificador> obtenerRecetasPorUsuarios() {
    List<Planificador> recetasAsignadas = new ArrayList<>();
    objMySqlConnection.open();

    // Consulta para obtener todas las recetas con sus usuarios asignados
    String sql = "SELECT r.id_receta, r.nombre_receta, r.descripcion, r.ingredientes, r.instrucciones, r.imagen, " +
                 "GROUP_CONCAT(u.nombre SEPARATOR ', ') AS usuarios " +
                 "FROM Planificador p " +
                 "JOIN Recetas r ON p.id_receta = r.id_receta " +
                 "JOIN Usuarios u ON p.id_usuario = u.id_usuario " +
                 "GROUP BY r.id_receta;";

    try (PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql)) {
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            // Crear un objeto Planificador para almacenar la receta asignada
            Planificador planificador = new Planificador();
            Recetas receta = new Recetas();
            receta.setIdReceta(rs.getInt("id_receta"));
            receta.setNombreReceta(rs.getString("nombre_receta"));
            receta.setDescripcion(rs.getString("descripcion"));
            receta.setIngredientes(rs.getString("ingredientes"));
            receta.setInstrucciones(rs.getString("instrucciones"));
            receta.setImagen(rs.getString("imagen"));
            planificador.setReceta(receta); // Asignar la receta al Planificador

            // Obtener los usuarios asignados a la receta
            String usuarios = rs.getString("usuarios");
            System.out.println(usuarios);
            planificador.setUsuarios(usuarios); // Asignar los usuarios al Planificador

            // Agregar el Planificador a la lista
            recetasAsignadas.add(planificador);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return recetasAsignadas; // Retornar todas las recetas asignadas
}




    
    
    
    
}

    
    
    
    



