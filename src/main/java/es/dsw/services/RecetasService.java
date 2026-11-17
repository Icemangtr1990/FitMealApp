package es.dsw.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.dsw.models.ItemCompra;
import es.dsw.models.Recetas;
import es.dsw.repositories.RecetasRepository;

@Service
public class RecetasService {

	private final RecetasRepository repository;
	private final ObjectMapper mapper = new ObjectMapper();

	public RecetasService(RecetasRepository repository) {
		this.repository = repository;
	}

	public List<Recetas> findAll() {
		return repository.findAll();
	}

	public boolean crear(Recetas receta) {
		return repository.insert(receta);
	}

	public boolean eliminar(int idReceta) {
		return repository.deleteById(idReceta);
	}

	public boolean asignar(int idReceta, int idUsuario, String fecha) {
		return repository.assign(idReceta, idUsuario, fecha);
	}

	public List<Recetas> findByUsuario(int idUsuario) {
		return repository.findByUsuario(idUsuario);
	}

	/** Lista de la compra con ingredientes agrupados y filtro opcional por fechas. */
	public List<ItemCompra> listaDeCompras(int idUsuario, String fechaDesde, String fechaHasta) {
		Map<String, ItemCompra> agrupados = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

		for (Recetas receta : repository.findShoppingRows(idUsuario, fechaDesde, fechaHasta)) {
			String json = receta.getIngredientes();
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
				item.anadir(ingrediente.get("cantidad"), receta.getNombreReceta());
			}
		}

		return new ArrayList<>(agrupados.values());
	}

}
