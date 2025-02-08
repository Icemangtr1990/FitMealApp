package es.dsw.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import es.dsw.connections.MySqlConnection;

public class Users {

     // Atributos del usuario
    private int idUser;
    private String nombre;
    private String passwd;
    private String userRole;
    private String email;
    
    // Conexión a la base de datos
    private final MySqlConnection objMySqlConnection;

    public Users() {
        objMySqlConnection = new MySqlConnection();
    }

    // Métodos Getter y Setter
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswd() { return passwd; }
    public void setPasswd(String passwd) { this.passwd = passwd; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

 

   

    public ArrayList<Users> getUser() {
        String sql = "SELECT id_usuario AS ID, nombre AS NOMBRE, password AS CONTRASEÑA, email AS EMAIL, rol AS ROL FROM recetas_app.usuarios";
        
        ArrayList<Users> objListaUsuarios = new ArrayList<>();
        objMySqlConnection.open();
    
        if (!objMySqlConnection.isError()) {
            ResultSet result = objMySqlConnection.executeSelect(sql);
            if (result == null) {
                System.out.println("El ResultSet es null. Error en la ejecución de la consulta.");
            } else {
                try {
                    while (result.next()) {
                        Users objUsuario = new Users();
                        objUsuario.setIdUser(result.getInt("ID")); // Agregamos la asignación del ID
                        objUsuario.setPasswd(result.getString("CONTRASEÑA"));
                        objUsuario.setNombre(result.getString("NOMBRE"));
                        objUsuario.setEmail(result.getString("EMAIL"));
                        objUsuario.setUserRole(result.getString("ROL"));
                        objListaUsuarios.add(objUsuario);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    objMySqlConnection.close();
                }
            }
        } else {
            System.out.println("Error en conexión: " + objMySqlConnection.msgError());
        }
        return objListaUsuarios;
    }
    

    

    public Users getUserByUsername(String username) {
    // Usamos una consulta parametrizada para obtener un solo usuario por su nombre de usuario
    String sql = "SELECT id_usuario, nombre, password AS CONTRASEÑA, email AS EMAIL, rol AS ROL FROM recetas_app.usuarios WHERE nombre = ?";
    
    Users objUsuario = null;
    objMySqlConnection.open();
    
    if (!objMySqlConnection.isError()) {
        try {
            // Usamos un PreparedStatement para evitar inyecciones SQL
            PreparedStatement stmt = objMySqlConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, username);  // Seteamos el valor del nombre de usuario

            ResultSet result = stmt.executeQuery();
            
            if (result != null && result.next()) {
                objUsuario = new Users();
                objUsuario.setIdUser(result.getInt("id_usuario"));
                objUsuario.setNombre(result.getString("nombre"));
                objUsuario.setPasswd(result.getString("CONTRASEÑA"));
                objUsuario.setEmail(result.getString("EMAIL"));
                objUsuario.setUserRole(result.getString("ROL"));
            } else {
                System.out.println("No se encontró el usuario con el nombre: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            objMySqlConnection.close();
        }
    } else {
        System.out.println("Error en conexión: " + objMySqlConnection.msgError());
    }
    
    return objUsuario;
}


public boolean insertUser() {
    String sql = "INSERT INTO usuarios (nombre, password, email, rol) " +
                 "VALUES ('" + this.nombre + "', " +
                 "'" + this.passwd + "', " +
                 "'" + this.email + "', " +
                 "'" + this.userRole + "');";

    objMySqlConnection.open();  // Abrir la conexión a la base de datos
    
    if (!objMySqlConnection.isError()) {
        System.out.println(sql);  // Para depuración, muestra la consulta generada
        objMySqlConnection.executeInsert(sql);  // Ejecutar la inserción
        objMySqlConnection.close();  // Cerrar la conexión
        return true;  // Si la inserción fue exitosa
    } else {
        System.out.println("Error en conexión: " + objMySqlConnection.msgError());
    }

    return false;  // Si hubo error en la inserción
}

public boolean eliminarUsuario(int idUsuario) {
    String sql = "DELETE FROM usuarios WHERE id_usuario = " + idUsuario;
    
    objMySqlConnection.open();  // Abrir la conexión a la base de datos
    
    if (!objMySqlConnection.isError()) {
        System.out.println(sql);  // Para depuración, muestra la consulta generada
        objMySqlConnection.executeInsert(sql);  // Ejecutar la eliminación
        objMySqlConnection.close();  // Cerrar la conexión
        return true;  // Si la eliminación fue exitosa
    } else {
        System.out.println("Error en conexión: " + objMySqlConnection.msgError());
    }
    
    return false;  // Si hubo error al eliminar
}

    


}
