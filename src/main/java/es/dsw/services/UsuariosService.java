package es.dsw.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.dsw.models.Users;
import es.dsw.repositories.UsuariosRepository;

@Service
public class UsuariosService {

	private final UsuariosRepository repository;

	public UsuariosService(UsuariosRepository repository) {
		this.repository = repository;
	}

	public List<Users> findAll() {
		return repository.findAll();
	}

	public Users findByUsername(String username) {
		return repository.findByUsername(username);
	}

	@Transactional
	public boolean crear(Users usuario) {
		return repository.insert(usuario);
	}

	@Transactional
	public boolean eliminar(int idUsuario) {
		if (!repository.existsById(idUsuario)) {
			return false;
		}
		repository.deleteById(idUsuario);
		return true;
	}

	@Transactional
	public void actualizarPassword(int idUsuario, String hash) {
		repository.updatePassword(idUsuario, hash);
	}

}
