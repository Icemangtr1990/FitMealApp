package es.dsw.models;

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


    // Obtener todos los usuarios
    // public ArrayList<Users> getUser() {
    //     String sql = "SELECT nombre AS NOMBRE, "
    //                + "contraseña AS CONTRASEÑA,  "
    //                +  "email AS EMAIL , rol AS ROL"
    //                + "FROM recetas_app.usuarios";
        
    //     ArrayList<Users> objListaUsuarios = new ArrayList<>();
    //     objMySqlConnection.open();
        

    //     if (!objMySqlConnection.isError()) {
    //         ResultSet result = objMySqlConnection.executeSelect(sql);
    //         System.out.println(result);
    //         try {
    //             while (result.next()) {
    //                 Users objUsuario = new Users();
                                      
    //                 objUsuario.setPasswd(result.getString("CONTRASEÑA"));
    //                 objUsuario.setNombre(result.getString("NOMBRE"));                  
    //                 objUsuario.setEmail(result.getString("EMAIL"));
    //                 objUsuario.setUserRole(result.getString("ROL"));
    //                 objListaUsuarios.add(objUsuario);
    //             }
    //         } catch (SQLException e) {
    //             e.printStackTrace();
    //         } finally {
                
    //             objMySqlConnection.close();
    //         }
    //     }
    //     System.out.println("objListaUsuarios");
    //     return objListaUsuarios;
        
    // }

    public ArrayList<Users> getUser() {
        String sql = "SELECT nombre AS NOMBRE, contraseña AS CONTRASEÑA, email AS EMAIL, rol AS ROL FROM recetas_app.usuarios";
        
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
    


}
