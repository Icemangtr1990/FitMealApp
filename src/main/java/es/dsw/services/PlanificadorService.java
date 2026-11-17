package es.dsw.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.dsw.models.Planificador;
import es.dsw.repositories.PlanificadorRepository;

@Service
public class PlanificadorService {

	private final PlanificadorRepository repository;

	public PlanificadorService(PlanificadorRepository repository) {
		this.repository = repository;
	}

	public List<Planificador> findRecetasConUsuarios() {
		return repository.findRecetasConUsuarios();
	}

	@Transactional
	public boolean eliminarAsignacion(int idUsuario, int idReceta) {
		return repository.deleteAsignacion(idUsuario, idReceta);
	}

}
