package es.dsw.controllers;



import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.dsw.models.Recetas;
import es.dsw.models.Users;


@Controller
@SessionAttributes({"DataUser","usuario","rol","idUsuario"})
public class MainController {

    
	
	@GetMapping(value= {"/loggin"})
	public String loggin() {
		
		return "loggin";
	}

	@GetMapping(value = {"/", "/index"})
    public String idx(Model model) {
   
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        String nombreUsuario = null;
        String rolUsuario = null;
        int idUsuario = -1;  // Inicializamos en -1 por si no encontramos el usuario
    
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
                nombreUsuario = ((UserDetails) principal).getUsername();
                rolUsuario = ((UserDetails) principal).getAuthorities().toString();
                
                // Obtener el ID del usuario
                Users user = new Users();
                Users usuarioEncontrado = user.getUserByUsername(nombreUsuario);  // Obtener el usuario por su nombre
                if (usuarioEncontrado != null) {
                    idUsuario = usuarioEncontrado.getIdUser();  // Guardamos el ID del usuario
                }
            } else {
                nombreUsuario = principal.toString();
            }
        }
    
        // Pasar el usuario, rol y idUsuario al modelo
        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("rol", rolUsuario);
        model.addAttribute("idUsuario", idUsuario);  // Añadimos el ID al modelo
    
        return "index";
    }

     @GetMapping("/recetas")
    public String mostrarRecetas(Model model) {

        String usuario = (String) model.getAttribute("usuario");

        
        String rol = (String) model.getAttribute("rol");
        int idUsuario = (int) model.getAttribute("idUsuario");
       
        Recetas receta = new Recetas();
        List<Recetas> listaRecetas = receta.obtenerTodasLasRecetas(); // Obtener todas las recetas
        
        model.addAttribute("listaRecetas", listaRecetas); // Pasar las recetas al modelo
        
        return "recetas"; // Renderizar la plantilla recetas.html


     }

//    @GetMapping("/agregarReceta")
//     public String agregarReceta(@RequestParam("nombre") String nombreReceta,
//                              @RequestParam("descripcion") String descripcion,
//                              @RequestParam("ingredientes") String ingredientes,
//                              @RequestParam("instrucciones") String instrucciones,
//                              @RequestParam(value = "imagen", required = false) String imagen) {
    
//     // Crear una nueva instancia de Recetas y setear los valores
    
//     // Convertir la lista de ingredientes en formato JSON
//     // Separar los ingredientes por coma
//     String[] ingredientesArray = ingredientes.split(","); // Esto convierte la cadena en un array de ingredientes

//     // Crear un JSON con los ingredientes
//     StringBuilder ingredientesJson = new StringBuilder("{");

//     for (int i = 0; i < ingredientesArray.length; i++) {
//         // Limpiar los espacios extra de cada ingrediente
//         String ingrediente = ingredientesArray[i].trim();
//         ingredientesJson.append("\"ingrediente" + (i + 1) + "\": \"" + ingrediente + "\"");
        
//         // Si no es el último ingrediente, añadir coma
//         if (i < ingredientesArray.length - 1) {
//             ingredientesJson.append(", ");
//         }
//     }

//     // Cerrar el JSON
//     ingredientesJson.append("}");

//     // Asignar los valores a la receta
//     Recetas nuevaReceta = new Recetas();
//     nuevaReceta.setNombreReceta(nombreReceta);
//     nuevaReceta.setDescripcion(descripcion);
//     nuevaReceta.setInstrucciones(instrucciones);
//     nuevaReceta.setIngredientes(ingredientesJson.toString());  // Setear los ingredientes en formato JSON
//     nuevaReceta.setImagen(imagen != null && !imagen.trim().isEmpty() ? imagen : null);  // Si la imagen está vacía, asignar null
//     nuevaReceta.setIdUsuario(1);  // Aquí puedes poner el ID del usuario autenticado si lo tienes

//     // Intentar insertar la receta en la base de datos
//     boolean success = nuevaReceta.insertReceta();

//     if (success) {
//         // Si la inserción fue exitosa
//         System.out.println("Receta insertada con éxito ");
//         return "redirect:/recetas";  // Redirigir a una página de éxito 
//     } else {
//         // Si hubo algún error
//         System.out.println("Error al insertar la receta.");
//         return "error";  // Redirigir a una página de error (error.html)
//     }
// }

@GetMapping("/agregarReceta")
public String agregarReceta(@RequestParam("nombre") String nombreReceta,
                             @RequestParam("descripcion") String descripcion,
                             @RequestParam("cantidad") String[] cantidades,  // Recibir un array de cantidades
                             @RequestParam("ingrediente") String[] ingredientes,  // Recibir un array de ingredientes
                             @RequestParam("instrucciones") String instrucciones,
                             @RequestParam(value = "imagen", required = false) String imagen) {

    // Crear una lista de ingredientes en formato JSON
    StringBuilder ingredientesJson = new StringBuilder("[");

    for (int i = 0; i < cantidades.length; i++) {
        // Limpiar los espacios extra
        String cantidad = cantidades[i].trim();
        String ingrediente = ingredientes[i].trim();
        
        // Construir cada objeto ingrediente en JSON
        ingredientesJson.append("{")
                        .append("\"cantidad\": \"").append(cantidad).append("\", ")
                        .append("\"ingrediente\": \"").append(ingrediente).append("\"")
                        .append("}");

        // Si no es el último ingrediente, añadir coma
        if (i < cantidades.length - 1) {
            ingredientesJson.append(", ");
        }
    }

    // Cerrar el JSON
    ingredientesJson.append("]");

    // Asignar los valores a la receta
    Recetas nuevaReceta = new Recetas();
    nuevaReceta.setNombreReceta(nombreReceta);
    nuevaReceta.setDescripcion(descripcion);
    nuevaReceta.setInstrucciones(instrucciones);
    nuevaReceta.setIngredientes(ingredientesJson.toString());  // Setear los ingredientes en formato JSON
    nuevaReceta.setImagen(imagen != null && !imagen.trim().isEmpty() ? imagen : null);  // Si la imagen está vacía, asignar null
    nuevaReceta.setIdUsuario(1);  // Aquí puedes poner el ID del usuario autenticado si lo tienes

    // Intentar insertar la receta en la base de datos
    boolean success = nuevaReceta.insertReceta();

    if (success) {
        // Si la inserción fue exitosa
        System.out.println("Receta insertada con éxito ");
        return "redirect:/recetas";  // Redirigir a una página de éxito 
    } else {
        // Si hubo algún error
        System.out.println("Error al insertar la receta.");
        return "error";  // Redirigir a una página de error (error.html)
    }
}


@PostMapping("/admin/eliminar-receta")
public String eliminarReceta(@RequestParam("idReceta") int idReceta) {
    Recetas receta = new Recetas();
    receta.eliminarReceta(idReceta);  // Llamamos al método para eliminar la receta
    return "redirect:/recetas"; // Redirigimos a la lista de recetas
}

    

    @GetMapping("/planificador")
    public String mostrarPlanificador() {
        return "planificador"; // Este archivo deberá estar en /resources/templates
    }

    @GetMapping("/compras")
    public String mostrarListaCompras() {
        return "compras"; // Este archivo deberá estar en /resources/templates
    }

    @GetMapping("/crearUsuario")
    public String mostrarFormularioCrearUsuario() {
        return "crearUsuario";  // Nombre del HTML
    }

  
    @PostMapping("/admin/crear-usuario")
public String crearUsuario(@RequestParam String nombre, 
                           @RequestParam String passwd, 
                           @RequestParam String email, 
                           @RequestParam String userRole, 
                           RedirectAttributes redirectAttributes) {

    Users nuevoUsuario = new Users();
    nuevoUsuario.setNombre(nombre);
    nuevoUsuario.setPasswd(passwd);  // Sin cifrar
    nuevoUsuario.setEmail(email);
    nuevoUsuario.setUserRole(userRole);

    if (nuevoUsuario.insertUser()) {
        redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
    } else {
        redirectAttributes.addFlashAttribute("error", "Error al crear el usuario");
    }

    return "redirect:/crearUsuario"; // Redirigir de vuelta al formulario
}

@GetMapping("/admin/listar-usuarios")
public String listarUsuarios(Model model) {
    Users user = new Users();
    ArrayList<Users> listaUsuarios = user.getUser();  // Obtiene la lista de usuarios

    model.addAttribute("usuarios", listaUsuarios);  // Pasamos los usuarios al modelo
    return "listar-usuarios";  // Nombre de la vista donde se mostrarán los usuarios
}

@PostMapping("/admin/eliminar-usuario/{id}")
public String eliminarUsuario(@PathVariable("id") int idUsuario, RedirectAttributes redirectAttributes) {
    Users user = new Users();
    
    boolean eliminado = user.eliminarUsuario(idUsuario);  // Llamamos al método de eliminación
    
    if (eliminado) {
        redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
    } else {
        redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario");
    }

    return "redirect:/admin/listar-usuarios";  // Redirigimos a la página de listado de usuarios
}

    

}

