package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IAdminReportService;
import com.unla.epicentrogourmet.services.IFestivalService;

@Controller
@RequestMapping("/admin/festivales")
public class AdminFestivalesController {

	private final IFestivalService festivalService;
	private final IAdminReportService adminReportService;

	public AdminFestivalesController(IFestivalService festivalService, IAdminReportService adminReportService) {
		this.festivalService = festivalService;
		this.adminReportService = adminReportService;
	}

	@GetMapping
	public String festivales(Model model) {
		model.addAttribute("festivales", festivalService.getAll());
		return ViewRouteHelper.ADMIN_FESTIVALES;
	}

	@GetMapping("/{id}/unidades")
	public String unidades(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		try {
			model.addAttribute("festival", adminReportService.obtenerFestival(id));
			model.addAttribute("unidades", adminReportService.unidadesDelFestival(id));
			return ViewRouteHelper.ADMIN_UNIDADES_FESTIVAL;
		} catch (IllegalArgumentException ex) {
			redirect.addFlashAttribute("error", ex.getMessage());
			return "redirect:/admin/festivales";
		}
	}
}
