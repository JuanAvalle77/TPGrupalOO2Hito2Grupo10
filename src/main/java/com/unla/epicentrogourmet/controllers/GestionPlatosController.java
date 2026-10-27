package com.unla.epicentrogourmet.controllers;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unla.epicentrogourmet.dtos.PlatoForm;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IPlatoService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/responsable/platos")
public class GestionPlatosController {

	private final IPlatoService platoService;
	private final SesionService sesionService;

	public GestionPlatosController(IPlatoService platoService, SesionService sesionService) {
		this.platoService = platoService;
		this.sesionService = sesionService;
	}

	@GetMapping
	public String lista(Model model) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		UnidadVenta seleccionada = unidades.isEmpty() ? null : unidades.get(0);
		model.addAttribute("unidades", unidades);
		model.addAttribute("unidadSeleccionada", seleccionada);
		model.addAttribute("platos", seleccionada == null ? List.of() : platoService.listar(seleccionada));
		return ViewRouteHelper.RESPONSABLE_PLATOS;
	}

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		return formulario(model, new PlatoForm(), null);
	}

	@PostMapping("/nuevo")
	public String guardar(@Valid @ModelAttribute("platoForm") PlatoForm form, BindingResult result,
			Model model, RedirectAttributes redirect) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		if (!result.hasErrors()) {
			try {
				platoService.crear(form, unidades);
				redirect.addFlashAttribute("ok", "Plato agregado al menú.");
				return "redirect:/responsable/platos";
			} catch (IllegalArgumentException ex) {
				result.reject("regla", ex.getMessage());
			}
		}
		return formulario(model, form, result);
	}

	private String formulario(Model model, PlatoForm form, BindingResult result) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		if (form.getUnidadId() == null && unidades.size() == 1) {
			form.setUnidadId(unidades.get(0).getId());
		}
		model.addAttribute("platoForm", form);
		model.addAttribute("unidades", unidades);
		model.addAttribute("errores", result == null ? List.of() : result.getGlobalErrors());
		return ViewRouteHelper.RESPONSABLE_PLATO_FORM;
	}
}
