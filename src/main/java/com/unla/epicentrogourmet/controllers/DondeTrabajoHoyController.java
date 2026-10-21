package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.implementation.DondeTrabajoHoyService;

/** Pantalla de las unidades donde le toca trabajar al empleado hoy. */
@Controller
@RequestMapping("/empleado/hoy")
public class DondeTrabajoHoyController {
	private final DondeTrabajoHoyService dondeTrabajoHoyService;

	public DondeTrabajoHoyController(DondeTrabajoHoyService dondeTrabajoHoyService) {
		this.dondeTrabajoHoyService = dondeTrabajoHoyService;
	}

	@GetMapping
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_HOY);
		mAV.addObject("asignaciones", dondeTrabajoHoyService.buscarAsignacionesDeHoy());
		return mAV;
	}
}
