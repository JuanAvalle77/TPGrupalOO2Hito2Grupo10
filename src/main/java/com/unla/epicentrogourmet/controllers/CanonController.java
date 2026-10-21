package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.services.ICanonService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

/** CU Calculo de canon: el responsable ve el canon de su(s) unidad(es). */
@Controller
@RequestMapping("/responsable/canon")
public class CanonController {

	private static final String VISTA_CANON = "responsable/canon";

	private final ICanonService canonService;
	private final SesionService sesionService;

	public CanonController(ICanonService canonService, SesionService sesionService) {
		this.canonService = canonService;
		this.sesionService = sesionService;
	}

	@GetMapping("")
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(VISTA_CANON);
		mAV.addObject("canones", canonService.calcularDetalle(sesionService.getUnidadesDelResponsable()));
		return mAV;
	}
}
