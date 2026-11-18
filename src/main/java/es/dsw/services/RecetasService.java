package es.dsw.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.dsw.models.ItemCompra;
import es.dsw.models.Planificador;
import es.dsw.models.Recetas;
import es.dsw.repositories.PlanificadorRecetaProjection;
import es.dsw.repositories.PlanificadorRepository;
import es.dsw.repositories.RecetasRepository;

@Service
public class RecetasService {

	private final RecetasRepository repository;
	private final PlanificadorRepository planificadorRepository;
	private final ObjectMapper mapper = new ObjectMapper();

	public RecetasService(RecetasRepository repository, PlanificadorRepository planificadorRepository) {
		this.repository = repository;
		this.planificadorRepository = planificadorRepository;
	}

	public List<Recetas> findAll() {
		return repository.findAll();
	}

	public Page<Recetas> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Recetas findById(int idReceta) {
		return repository.findById(idReceta).orElse(null);
	}

	@Transactional
	public boolean crear(Recetas receta) {
		return repository.insert(receta);
	}

	@Transactional
	public boolean eliminar(int idReceta) {
		repository.deleteById(idReceta);
		return true;
	}

	@Transactional
	public boolean asignar(int idReceta, int idUsuario, String fecha) {
		Planificador asignacion = new Planificador();
		asignacion.setIdReceta(idReceta);
		asignacion.setIdUsuario(idUsuario);
		asignacion.setFecha(parsearFecha(fecha));
		planificadorRepository.save(asignacion);
		return true;
	}

	public List<Recetas> findByUsuario(int idUsuario) {
		return repository.findRecetasByUsuario(idUsuario).stream()
				.map(this::toReceta)
				.toList();
	}

	/** Lista de la compra con ingredientes agrupados y filtro opcional por fechas. */
	public List<ItemCompra> listaDeCompras(int idUsuario, String fechaDesde, String fechaHasta) {
		Map<String, ItemCompra> agrupados = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		List<PlanificadorRecetaProjection> filas = repository.findShoppingRows(
				idUsuario, parsearFecha(fechaDesde), parsearFecha(fechaHasta));

		for (PlanificadorRecetaProjection fila : filas) {
			String json = fila.getIngredientes();
			if (json == null || json.isBlank()) {
				continue;
			}

			List<Map<String, String>> ingredientes;
			try {
				ingredientes = mapper.readValue(json, List.class);
			} catch (Exception e) {
				continue;
			}

			for (Map<String, String> ingrediente : ingredientes) {
				String nombre = ingrediente.get("ingrediente");
				if (nombre == null || nombre.isBlank()) {
					continue;
				}
				ItemCompra item = agrupados.computeIfAbsent(nombre.trim(), ItemCompra::new);
				item.anadir(ingrediente.get("cantidad"), fila.getNombreReceta());
			}
		}

		return new ArrayList<>(agrupados.values());
	}

	private Recetas toReceta(PlanificadorRecetaProjection fila) {
		Recetas receta = new Recetas();
		receta.setIdReceta(fila.getIdReceta());
		receta.setNombreReceta(fila.getNombreReceta());
		receta.setDescripcion(fila.getDescripcion());
		receta.setIngredientes(fila.getIngredientes());
		receta.setInstrucciones(fila.getInstrucciones());
		receta.setImagen(fila.getImagen());
		if (fila.getFecha() != null) {
			receta.setFecha(new Date(fila.getFecha().getTime()));
		}
		return receta;
	}

	private LocalDate parsearFecha(String fecha) {
		if (fecha == null || fecha.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(fecha);
		} catch (Exception e) {
			return null;
		}
	}

}
