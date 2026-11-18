package es.dsw.controllers;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.dsw.dtos.ApiError;
import es.dsw.dtos.IngredienteDTO;
import es.dsw.dtos.PaginaDTO;
import es.dsw.dtos.RecetaDTO;
import es.dsw.dtos.RecetaRequest;
import es.dsw.dtos.UsuarioDTO;
import es.dsw.models.ItemCompra;
import es.dsw.models.Recetas;
import es.dsw.models.Users;
import es.dsw.services.RecetasService;
import es.dsw.services.UsuariosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "FitMealApp", description = "API REST del planificador de comidas")
public class ApiController {

	private final RecetasService recetasService;
	private final UsuariosService usuariosService;
	private final ObjectMapper mapper = new ObjectMapper();

	public ApiController(RecetasService recetasService, UsuariosService usuariosService) {
		this.recetasService = recetasService;
		this.usuariosService = usuariosService;
	}

	@GetMapping("/recetas")
	@Operation(summary = "Listar recetas (paginado)")
	public PaginaDTO<RecetaDTO> listarRecetas(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "idReceta") String sort) {

		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(sort));
		Page<RecetaDTO> pagina = recetasService.findAll(pageable).map(this::toDTO);
		return new PaginaDTO<>(pagina.getContent(), pagina.getNumber(), pagina.getSize(),
				pagina.getTotalElements(), pagina.getTotalPages());
	}

	@GetMapping("/recetas/{id}")
	@Operation(summary = "Obtener una receta por id")
	public ResponseEntity<Object> receta(@PathVariable int id) {
		Recetas receta = recetasService.findById(id);
		return receta == null
				? ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiError.of(404, "No encontrado", "No existe una receta con id " + id))
				: ResponseEntity.ok(toDTO(receta));
	}

	@GetMapping("/usuarios")
	@Operation(summary = "Listar usuarios (solo admin)")
	public List<UsuarioDTO> usuarios() {
		return usuariosService.findAll().stream()
				.map(u -> new UsuarioDTO(u.getIdUser(), u.getNombre(), u.getEmail(), u.getUserRole()))
				.toList();
	}

	@GetMapping("/compras")
	@Operation(summary = "Lista de la compra del usuario autenticado")
	public List<ItemCompra> compras(
			@RequestParam(required = false) String fechaDesde,
			@RequestParam(required = false) String fechaHasta,
			Authentication authentication) {

		Users usuario = usuarioAutenticado(authentication);
		return usuario == null ? List.of() : recetasService.listaDeCompras(usuario.getIdUser(), fechaDesde, fechaHasta);
	}

	@PostMapping("/recetas")
	@Operation(summary = "Crear receta (solo admin)")
	public ResponseEntity<Void> crear(@Valid @RequestBody RecetaRequest request, Authentication authentication) {
		Users autor = usuarioAutenticado(authentication);
		if (autor == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		List<Map<String, String>> ingredientes = request.ingredientes().stream()
				.map(i -> Map.of("cantidad", i.cantidad(), "ingrediente", i.ingrediente()))
				.toList();

		String json;
		try {
			json = mapper.writeValueAsString(ingredientes);
		} catch (JsonProcessingException e) {
			return ResponseEntity.badRequest().build();
		}

		Recetas receta = new Recetas();
		receta.setNombreReceta(request.nombre());
		receta.setDescripcion(request.descripcion());
		receta.setInstrucciones(request.instrucciones());
		receta.setIngredientes(json);
		receta.setImagen(request.imagen());
		receta.setIdUsuario(autor.getIdUser());

		recetasService.crear(receta);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	private Users usuarioAutenticado(Authentication authentication) {
		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			return null;
		}
		String username = ((UserDetails) authentication.getPrincipal()).getUsername();
		return usuariosService.findByUsername(username);
	}

	private RecetaDTO toDTO(Recetas receta) {
		List<IngredienteDTO> ingredientes;
		try {
			ingredientes = mapper.readValue(receta.getIngredientes(),
					new TypeReference<List<Map<String, String>>>() { }).stream()
					.map(m -> new IngredienteDTO(m.get("cantidad"), m.get("ingrediente")))
					.toList();
		} catch (Exception e) {
			ingredientes = List.of();
		}

		return new RecetaDTO(receta.getIdReceta(), receta.getNombreReceta(), receta.getDescripcion(),
				receta.getInstrucciones(), ingredientes, receta.getImagen(), receta.getFechaFormateada());
	}

}
