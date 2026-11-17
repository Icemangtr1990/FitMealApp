package es.dsw.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Entidad usuario. Spring Data JPA se encarga del acceso a datos. */
@Entity
@Table(name = "usuarios")
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private int idUser;

	@Column(nullable = false, unique = true)
	private String nombre;

	@Column(name = "password", nullable = false)
	private String passwd;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(name = "rol")
	private String userRole;

	public Users() {
	}

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
