package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;

/** Zona del Empleado (cocina o cajero). Los casos de uso de empleado se agregan aca. */
@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

	@GetMapping("")
	public ModelAndView index() {
		return new ModelAndView(ViewRouteHelper.EMPLEADO_INDEX);
	}
}
