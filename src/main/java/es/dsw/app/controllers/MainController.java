package es.dsw.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"DataUser"})
public class MainController {
	
	@GetMapping(value= {"/loggin"})
	public String loggin() {
		
		return "loggin";
	}

	@GetMapping(value= {"/","/index"})
	public String idx() {
		
		return "index";
	}
    @GetMapping("/recetas")
    public String mostrarRecetas() {
        // Por ahora, redirigimos a una página vacía
        return "recetas"; // Este archivo deberá estar en /resources/templates
    }

    @GetMapping("/planificador")
    public String mostrarPlanificador() {
        return "planificador"; // Este archivo deberá estar en /resources/templates
    }

    @GetMapping("/compras")
    public String mostrarListaCompras() {
        return "compras"; // Este archivo deberá estar en /resources/templates
    }
}
