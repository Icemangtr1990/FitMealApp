package es.dsw.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.dsw.models.Planificador;
import es.dsw.models.Recetas;
import es.dsw.repositories.PlanificadorRepository;
import es.dsw.repositories.RecetaConUsuariosProjection;

@Service
public class PlanificadorService {

	private final PlanificadorRepository repository;

	public PlanificadorService(PlanificadorRepository repository) {
		this.repository = repository;
	}

	public List<Planificador> findRecetasConUsuarios() {
		return repository.findRecetasConUsuarios().stream()
				.map(this::toPlanificador)
				.toList();
	}

	@Transactional
	public boolean eliminarAsignacion(int idUsuario, int idReceta) {
		repository.deleteByIdUsuarioAndIdReceta(idUsuario, idReceta);
		return true;
	}

	private Planificador toPlanificador(RecetaConUsuariosProjection fila) {
		Recetas receta = new Recetas();
		receta.setIdReceta(fila.getIdReceta());
		receta.setNombreReceta(fila.getNombreReceta());
		receta.setDescripcion(fila.getDescripcion());
		receta.setIngredientes(fila.getIngredientes());
		receta.setInstrucciones(fila.getInstrucciones());
		receta.setImagen(fila.getImagen());

		Planificador planificador = new Planificador();
		planificador.setReceta(receta);
		planificador.setUsuarios(fila.getUsuarios());
		return planificador;
	}

}
