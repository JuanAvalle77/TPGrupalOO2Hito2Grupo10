package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.services.ILiquidacionService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

/** CU Liquidacion de haberes: el responsable ve el sueldo de cada empleado de su unidad. */
@Controller
@RequestMapping("/responsable/haberes")
public class LiquidacionController {

	private static final String VISTA_HABERES = "responsable/haberes";

	private final ILiquidacionService liquidacionService;
	private final SesionService sesionService;

	public LiquidacionController(ILiquidacionService liquidacionService, SesionService sesionService) {
		this.liquidacionService = liquidacionService;
		this.sesionService = sesionService;
	}

	@GetMapping("")
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(VISTA_HABERES);
		mAV.addObject("unidades", liquidacionService.liquidarUnidades(sesionService.getUnidadesDelResponsable()));
		return mAV;
	}
}
