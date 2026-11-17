package es.dsw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.dsw.models.Users;

public interface UsuariosRepository extends JpaRepository<Users, Integer> {

	Users findByNombre(String nombre);

	default Users findByUsername(String username) {
		return findByNombre(username);
	}

	default boolean insert(Users usuario) {
		return save(usuario) != null;
	}

	@Modifying
	@Query("UPDATE Users u SET u.passwd = :hash WHERE u.idUser = :id")
	void updatePassword(@Param("id") int id, @Param("hash") String hash);

}
