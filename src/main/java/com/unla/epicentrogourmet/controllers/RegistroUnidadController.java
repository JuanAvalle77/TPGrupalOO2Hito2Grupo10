package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unla.epicentrogourmet.dtos.RegistroUnidadForm;
import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;
import com.unla.epicentrogourmet.services.IRegistroUnidadService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/registro")
public class RegistroUnidadController {

	private final IFestivalService festivalService;
	private final IRegistroUnidadService registroUnidadService;

	public RegistroUnidadController(IFestivalService festivalService, IRegistroUnidadService registroUnidadService) {
		this.festivalService = festivalService;
		this.registroUnidadService = registroUnidadService;
	}

	@GetMapping
	public String formulario(Model model) {
		if (!model.containsAttribute("registroUnidadForm")) {
			model.addAttribute("registroUnidadForm", new RegistroUnidadForm());
		}
		cargarOpciones(model);
		return ViewRouteHelper.REGISTRO_UNIDAD;
	}

	@PostMapping
	public String registrar(@Valid @ModelAttribute("registroUnidadForm") RegistroUnidadForm form,
			BindingResult binding, Model model, RedirectAttributes redirectAttributes) {
		if (!binding.hasErrors()) {
			try {
				registroUnidadService.registrar(form);
				redirectAttributes.addFlashAttribute("registroExitoso", true);
				return "redirect:/login";
			} catch (IllegalArgumentException ex) {
				model.addAttribute("errorRegistro", ex.getMessage());
			}
		}
		cargarOpciones(model);
		return ViewRouteHelper.REGISTRO_UNIDAD;
	}

	private void cargarOpciones(Model model) {
		model.addAttribute("festivales", festivalService.getAll());
		model.addAttribute("tiposEmpleado", TipoEmpleado.values());
		model.addAttribute("rolesCocina", RolCocina.values());
		model.addAttribute("turnos", Turno.values());
	}
}
