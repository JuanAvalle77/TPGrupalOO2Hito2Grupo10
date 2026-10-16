package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;

/** Zona del Administrador. Cada caso de uso de admin agrega sus endpoints en este controlador. */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@GetMapping("")
	public ModelAndView index() {
		return new ModelAndView(ViewRouteHelper.ADMIN_INDEX);
	}
}
