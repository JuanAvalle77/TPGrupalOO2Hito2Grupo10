package com.unla.epicentrogourmet.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.implementation.ReciboSueldoService;

/** Consulta de haberes propios y descarga de recibos. */
@Controller
@RequestMapping("/empleado/sueldos")
public class ReciboSueldoController {
	private final ReciboSueldoService reciboSueldoService;

	public ReciboSueldoController(ReciboSueldoService reciboSueldoService) {
		this.reciboSueldoService = reciboSueldoService;
	}

	@GetMapping
	public ModelAndView listar() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.EMPLEADO_SUELDOS);
		mAV.addObject("recibos", reciboSueldoService.listarRecibos());
		return mAV;
	}

	@GetMapping("/{festivalId}/recibo")
	public ResponseEntity<byte[]> descargar(@PathVariable Long festivalId) {
		byte[] pdf = reciboSueldoService.generarRecibo(festivalId);
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recibo-" + festivalId + ".pdf\"")
				.body(pdf);
	}
}
