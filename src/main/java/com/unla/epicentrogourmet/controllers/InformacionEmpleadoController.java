package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.implementation.InformacionEmpleadoService;

@Controller
@RequestMapping("/empleado/mi-informacion")
public class InformacionEmpleadoController {
	private final InformacionEmpleadoService informacionService;

	public InformacionEmpleadoController(InformacionEmpleadoService informacionService) {
		this.informacionService = informacionService;
	}

	@GetMapping
	public String informacion(Model model) {
		model.addAttribute("asignaciones", informacionService.buscarUnidadesYFestivales());
		return ViewRouteHelper.EMPLEADO_INFORMACION;
	}
}
