package com.unla.epicentrogourmet.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;
import com.unla.epicentrogourmet.services.IReportesResponsableService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

@Controller
@RequestMapping("/responsable")
public class ReportesResponsableController {

	private final IFestivalService festivalService;
	private final IReportesResponsableService reportesService;
	private final SesionService sesionService;

	public ReportesResponsableController(IFestivalService festivalService,
			IReportesResponsableService reportesService, SesionService sesionService) {
		this.festivalService = festivalService;
		this.reportesService = reportesService;
		this.sesionService = sesionService;
	}

	@GetMapping("/reporte-recaudacion")
	public String recaudacion(@RequestParam(required = false) Long festivalId, Model model) {
		model.addAttribute("festivales", festivalService.getAll());
		if (festivalId != null) {
			try {
				model.addAttribute("reporte", reportesService.reporteRecaudacion(festivalId));
				model.addAttribute("festivalId", festivalId);
			} catch (IllegalArgumentException ex) {
				model.addAttribute("error", ex.getMessage());
			}
		}
		return ViewRouteHelper.RESPONSABLE_REPORTE_RECAUDACION;
	}

	@GetMapping("/rentabilidad")
	public String rentabilidad(@RequestParam(required = false) Long unidadId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
			Model model) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		model.addAttribute("unidades", unidades);
		model.addAttribute("unidadId", unidadId);
		model.addAttribute("desde", desde);
		model.addAttribute("hasta", hasta);
		if (unidadId != null) {
			try {
				model.addAttribute("rentabilidad", reportesService.rentabilidad(unidadId, desde, hasta, unidades));
			} catch (IllegalArgumentException ex) {
				model.addAttribute("error", ex.getMessage());
			}
		}
		return ViewRouteHelper.RESPONSABLE_RENTABILIDAD;
	}

	@GetMapping("/plato-estrella")
	public String platoEstrella(@RequestParam(required = false) Long unidadId, Model model) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		model.addAttribute("unidades", unidades);
		model.addAttribute("unidadId", unidadId);
		if (unidadId != null) {
			try {
				model.addAttribute("unidad", unidades.stream().filter(u -> u.getId().equals(unidadId)).findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Esa unidad no está a tu cargo")));
				model.addAttribute("plato", reportesService.platoEstrella(unidadId, unidades).orElse(null));
			} catch (IllegalArgumentException ex) {
				model.addAttribute("error", ex.getMessage());
			}
		}
		return ViewRouteHelper.RESPONSABLE_PLATO_ESTRELLA;
	}
}
