package es.dsw.models;

/** Entidad usuario. El acceso a datos está en UsuariosRepository. */
public class Users {

	private int idUser;
	private String nombre;
	private String passwd;
	private String userRole;
	private String email;

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

	@Override
	public String toString() {
		return "Users{idUser=" + idUser + ", nombre='" + nombre + "', email='" + email + "', rol='" + userRole + "'}";
	}

}
