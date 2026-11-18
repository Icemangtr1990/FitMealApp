package es.dsw.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.dsw.models.ItemCompra;
import es.dsw.models.Planificador;
import es.dsw.models.Recetas;
import es.dsw.models.Users;
import es.dsw.services.PlanificadorService;
import es.dsw.services.RecetasService;
import es.dsw.services.UsuariosService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@Validated
@SessionAttributes({"DataUser", "usuario", "rol", "idUsuario"})
public class MainController {

	private final UsuariosService usuariosService;
	private final RecetasService recetasService;
	private final PlanificadorService planificadorService;
	private final PasswordEncoder passwordEncoder;
	private final ObjectMapper mapper = new ObjectMapper();

	public MainController(UsuariosService usuariosService, RecetasService recetasService,
			PlanificadorService planificadorService, PasswordEncoder passwordEncoder) {
		this.usuariosService = usuariosService;
		this.recetasService = recetasService;
		this.planificadorService = planificadorService;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping("/loggin")
	public String loggin() {
		return "loggin";
	}

	@GetMapping(value = {"/", "/index"})
	public String idx(Model model, HttpSession session) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String nombreUsuario = null;
		String rolUsuario = null;
		int idUsuario = -1;

		if (auth != null && auth.getPrincipal() instanceof UserDetails) {
			UserDetails principal = (UserDetails) auth.getPrincipal();
			nombreUsuario = principal.getUsername();
			rolUsuario = principal.getAuthorities().toString();

			Users usuarioEncontrado = usuariosService.findByUsername(nombreUsuario);
			if (usuarioEncontrado != null) {
				idUsuario = usuarioEncontrado.getIdUser();
			}
		}

		model.addAttribute("usuario", nombreUsuario);
		model.addAttribute("rol", rolUsuario);
		model.addAttribute("idUsuario", idUsuario);
		session.setAttribute("idUsuario", idUsuario);

		return "index";
	}

	@GetMapping("/recetas")
	public String mostrarRecetas(
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "9") int tamano,
			Model model) {
		String usuario = (String) model.getAttribute("usuario");
		String rol = (String) model.getAttribute("rol");
		Integer idUsuarioObj = (Integer) model.getAttribute("idUsuario");
		int idUsuario = (idUsuarioObj != null) ? idUsuarioObj : -1;

		var paginaRecetas = recetasService.findAll(
				PageRequest.of(Math.max(pagina, 0), Math.max(tamano, 1), Sort.by("idReceta")));
		List<Recetas> listaRecetas = paginaRecetas.getContent();
		ArrayList<Users> listaUsuarios = new ArrayList<>(usuariosService.findAll());
		List<Planificador> recetasAsignadas = planificadorService.findRecetasConUsuarios();

		model.addAttribute("usuario", usuario);
		model.addAttribute("rol", rol);
		model.addAttribute("idUsuario", idUsuario);
		model.addAttribute("usuarios", listaUsuarios);
		model.addAttribute("listaRecetas", listaRecetas);
		model.addAttribute("paginaActual", paginaRecetas.getNumber());
		model.addAttribute("totalPaginas", paginaRecetas.getTotalPages());
		model.addAttribute("recetasAsignadas", recetasAsignadas);

		return "recetas";
	}

	@PostMapping("/agregarReceta")
	public String agregarReceta(@RequestParam("nombre") @NotBlank String nombreReceta,
			@RequestParam("descripcion") @NotBlank String descripcion,
			@RequestParam("cantidad") @NotEmpty String[] cantidades,
			@RequestParam("ingrediente") @NotEmpty String[] ingredientes,
			@RequestParam("instrucciones") @NotBlank String instrucciones,
			@RequestParam(value = "imagen", required = false) String imagen,
			RedirectAttributes redirectAttributes) {

		List<Map<String, String>> listaIngredientes = new ArrayList<>();
		int total = Math.min(cantidades.length, ingredientes.length);
		for (int i = 0; i < total; i++) {
			Map<String, String> ingrediente = new LinkedHashMap<>();
			ingrediente.put("cantidad", cantidades[i] == null ? "" : cantidades[i].trim());
			ingrediente.put("ingrediente", ingredientes[i] == null ? "" : ingredientes[i].trim());
			listaIngredientes.add(ingrediente);
		}

		String ingredientesJson;
		try {
			ingredientesJson = mapper.writeValueAsString(listaIngredientes);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("No se pudo serializar los ingredientes", e);
		}

		Recetas nuevaReceta = new Recetas();
		nuevaReceta.setNombreReceta(nombreReceta);
		nuevaReceta.setDescripcion(descripcion);
		nuevaReceta.setInstrucciones(instrucciones);
		nuevaReceta.setIngredientes(ingredientesJson);
		nuevaReceta.setImagen(imagen != null && !imagen.trim().isEmpty() ? imagen : null);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String nombreUsuario = (auth != null && auth.getPrincipal() instanceof UserDetails)
				? ((UserDetails) auth.getPrincipal()).getUsername() : null;
		Users autor = nombreUsuario != null ? usuariosService.findByUsername(nombreUsuario) : null;
		if (autor == null) {
			redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario.");
			return "redirect:/recetas";
		}
		nuevaReceta.setIdUsuario(autor.getIdUser());

		if (recetasService.crear(nuevaReceta)) {
			redirectAttributes.addFlashAttribute("mensaje", "Receta creada correctamente.");
			return "redirect:/recetas";
		}
		redirectAttributes.addFlashAttribute("error", "No se pudo crear la receta.");
		return "redirect:/recetas";
	}

	@PostMapping("/admin/eliminar-receta")
	public String eliminarReceta(@RequestParam("idReceta") int idReceta, RedirectAttributes redirectAttributes) {
		recetasService.eliminar(idReceta);
		redirectAttributes.addFlashAttribute("mensaje", "Receta eliminada correctamente.");
		return "redirect:/recetas";
	}

	@PostMapping("/asignarReceta")
	public String asignarReceta(@RequestParam("idReceta") int idReceta,
			@RequestParam("idUsuario") int idUsuario,
			@RequestParam("fecha") String fecha, RedirectAttributes redirectAttributes) {

		if (recetasService.asignar(idReceta, idUsuario, fecha)) {
			redirectAttributes.addFlashAttribute("mensaje", "Receta asignada correctamente.");
		} else {
			redirectAttributes.addFlashAttribute("error", "No se pudo asignar la receta.");
		}
		return "redirect:/recetas";
	}

	@PostMapping("/eliminarAsignacion")
	public String eliminarAsignacion(@RequestParam("idUsuario") int idUsuario,
			@RequestParam("idReceta") int idReceta, RedirectAttributes redirectAttributes) {

		if (planificadorService.eliminarAsignacion(idUsuario, idReceta)) {
			redirectAttributes.addFlashAttribute("mensaje", "Asignación eliminada con éxito.");
		} else {
			redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la asignación.");
		}
		return "redirect:/recetas";
	}

	@GetMapping("/planificador")
	public String mostrarPlanificador(Model model, HttpSession session) {
		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		if (idUsuario == null) {
			return "redirect:/index";
		}

		model.addAttribute("recetasAsignadas", recetasService.findByUsuario(idUsuario));
		model.addAttribute("iDusuario", idUsuario);
		return "planificador";
	}

	@GetMapping("/compras")
	public String mostrarListaCompras(
			@RequestParam(value = "fechaDesde", required = false) String fechaDesde,
			@RequestParam(value = "fechaHasta", required = false) String fechaHasta,
			Model model, HttpSession session) {

		Integer idUsuario = (Integer) session.getAttribute("idUsuario");
		if (idUsuario == null) {
			return "redirect:/index";
		}

		List<ItemCompra> listaCompras = recetasService.listaDeCompras(idUsuario, fechaDesde, fechaHasta);
		model.addAttribute("listaCompras", listaCompras);
		model.addAttribute("fechaDesde", fechaDesde);
		model.addAttribute("fechaHasta", fechaHasta);
		return "compras";
	}

	@GetMapping("/crearUsuario")
	public String mostrarFormularioCrearUsuario() {
		return "crearUsuario";
	}

	@PostMapping("/admin/crear-usuario")
	public String crearUsuario(@RequestParam @NotBlank String nombre,
			@RequestParam @NotBlank String passwd,
			@RequestParam @Email @NotBlank String email,
			@RequestParam @NotBlank String userRole,
			RedirectAttributes redirectAttributes) {

		Users nuevoUsuario = new Users();
		nuevoUsuario.setNombre(nombre);
		nuevoUsuario.setPasswd(passwordEncoder.encode(passwd));
		nuevoUsuario.setEmail(email);
		nuevoUsuario.setUserRole(userRole);

		if (usuariosService.crear(nuevoUsuario)) {
			redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
		} else {
			redirectAttributes.addFlashAttribute("error", "Error al crear el usuario");
		}
		return "redirect:/crearUsuario";
	}

	@GetMapping("/admin/listar-usuarios")
	public String listarUsuarios(Model model) {
		model.addAttribute("usuarios", usuariosService.findAll());
		return "listar-usuarios";
	}

	@PostMapping("/admin/eliminar-usuario/{id}")
	public String eliminarUsuario(@PathVariable("id") int idUsuario, RedirectAttributes redirectAttributes) {
		if (usuariosService.eliminar(idUsuario)) {
			redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
		} else {
			redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario");
		}
		return "redirect:/admin/listar-usuarios";
	}

}
