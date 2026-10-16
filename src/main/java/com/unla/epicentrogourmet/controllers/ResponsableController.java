package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;

/** Zona del Responsable de una unidad de venta. Los casos de uso de responsable se agregan aca. */
@Controller
@RequestMapping("/responsable")
public class ResponsableController {

	@GetMapping("")
	public ModelAndView index() {
		return new ModelAndView(ViewRouteHelper.RESPONSABLE_INDEX);
	}
}
