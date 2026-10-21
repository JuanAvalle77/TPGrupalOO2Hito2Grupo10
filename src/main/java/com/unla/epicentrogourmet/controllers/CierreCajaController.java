package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.ICierreCajaService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

/** Pantalla de recaudación y cierre diario, disponible solo para cajeros. */
@Controller
@RequestMapping("/empleado/cerrar-caja")
public class CierreCajaController {
	private final ICierreCajaService cierreCajaService;
	private final SesionService sesionService;

	public CierreCajaController(ICierreCajaService cierreCajaService, SesionService sesionService) {
		this.cierreCajaService = cierreCajaService;
		this.sesionService = sesionService;
	}

	@GetMapping
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_CERRAR_CAJA);
		Personal personal = sesionService.getPersonalActual();
		if (!(personal instanceof Cajero cajero)) {
			mAV.addObject("error", "El cierre de caja está disponible solo para cajeros.");
			return mAV;
		}
		mAV.addObject("estado", cierreCajaService.estadoDeHoy(cajero));
		return mAV;
	}

	@PostMapping
	public String cerrar(@RequestParam Long unidadId, RedirectAttributes redirectAttributes) {
		Personal personal = sesionService.getPersonalActual();
		if (!(personal instanceof Cajero cajero)) {
			redirectAttributes.addFlashAttribute("error", "El cierre de caja está disponible solo para cajeros.");
			return "redirect:/empleado/cerrar-caja";
		}
		try {
			cierreCajaService.cerrar(cajero, unidadId);
			redirectAttributes.addFlashAttribute("exito", "La caja de hoy se cerró correctamente.");
		} catch (IllegalArgumentException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
		}
		return "redirect:/empleado/cerrar-caja";
	}
}
