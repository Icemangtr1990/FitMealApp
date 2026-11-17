package es.dsw.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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

@Controller
@SessionAttributes({"DataUser", "usuario", "rol", "idUsuario"})
public class MainController {

	private final UsuariosService usuariosService;
	private final RecetasService recetasService;
	private final PlanificadorService planificadorService;
	private final PasswordEncoder passwordEncoder;

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
	public String mostrarRecetas(Model model) {
		String usuario = (String) model.getAttribute("usuario");
		String rol = (String) model.getAttribute("rol");
		Integer idUsuarioObj = (Integer) model.getAttribute("idUsuario");
		int idUsuario = (idUsuarioObj != null) ? idUsuarioObj : -1;

		List<Recetas> listaRecetas = recetasService.findAll();
		ArrayList<Users> listaUsuarios = new ArrayList<>(usuariosService.findAll());
		List<Planificador> recetasAsignadas = planificadorService.findRecetasConUsuarios();

		model.addAttribute("usuario", usuario);
		model.addAttribute("rol", rol);
		model.addAttribute("idUsuario", idUsuario);
		model.addAttribute("usuarios", listaUsuarios);
		model.addAttribute("listaRecetas", listaRecetas);
		model.addAttribute("recetasAsignadas", recetasAsignadas);

		return "recetas";
	}

	@PostMapping("/agregarReceta")
	public String agregarReceta(@RequestParam("nombre") String nombreReceta,
			@RequestParam("descripcion") String descripcion,
			@RequestParam("cantidad") String[] cantidades,
			@RequestParam("ingrediente") String[] ingredientes,
			@RequestParam("instrucciones") String instrucciones,
			@RequestParam(value = "imagen", required = false) String imagen) {

		StringBuilder ingredientesJson = new StringBuilder("[");
		for (int i = 0; i < cantidades.length; i++) {
			ingredientesJson.append("{\"cantidad\": \"").append(cantidades[i].trim())
					.append("\", \"ingrediente\": \"").append(ingredientes[i].trim()).append("\"}");
			if (i < cantidades.length - 1) {
				ingredientesJson.append(", ");
			}
		}
		ingredientesJson.append("]");

		Recetas nuevaReceta = new Recetas();
		nuevaReceta.setNombreReceta(nombreReceta);
		nuevaReceta.setDescripcion(descripcion);
		nuevaReceta.setInstrucciones(instrucciones);
		nuevaReceta.setIngredientes(ingredientesJson.toString());
		nuevaReceta.setImagen(imagen != null && !imagen.trim().isEmpty() ? imagen : null);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String nombreUsuario = (auth != null && auth.getPrincipal() instanceof UserDetails)
				? ((UserDetails) auth.getPrincipal()).getUsername() : null;
		Users autor = nombreUsuario != null ? usuariosService.findByUsername(nombreUsuario) : null;
		if (autor == null) {
			return "redirect:/recetas";
		}
		nuevaReceta.setIdUsuario(autor.getIdUser());

		return recetasService.crear(nuevaReceta) ? "redirect:/recetas" : "error";
	}

	@PostMapping("/admin/eliminar-receta")
	public String eliminarReceta(@RequestParam("idReceta") int idReceta) {
		recetasService.eliminar(idReceta);
		return "redirect:/recetas";
	}

	@PostMapping("/asignarReceta")
	public String asignarReceta(@RequestParam("idReceta") int idReceta,
			@RequestParam("idUsuario") int idUsuario,
			@RequestParam("fecha") String fecha, Model model) {

		model.addAttribute("usuarios", usuariosService.findAll());
		model.addAttribute("listaRecetas", recetasService.findAll());

		return recetasService.asignar(idReceta, idUsuario, fecha) ? "redirect:/recetas" : "error";
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
	public String crearUsuario(@RequestParam String nombre, @RequestParam String passwd,
			@RequestParam String email, @RequestParam String userRole,
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
