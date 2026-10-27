package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IAdminReportService;
import com.unla.epicentrogourmet.services.IFestivalService;

@Controller
@RequestMapping("/admin/mayor-canon")
public class AdminMayorCanonController {

	private final IFestivalService festivalService;
	private final IAdminReportService adminReportService;

	public AdminMayorCanonController(IFestivalService festivalService, IAdminReportService adminReportService) {
		this.festivalService = festivalService;
		this.adminReportService = adminReportService;
	}

	@GetMapping
	public String mayorCanon(@RequestParam(required = false) Long festivalId,
			@RequestParam(required = false, defaultValue = "3") Integer x, Model model) {
		model.addAttribute("festivales", festivalService.getAll());
		model.addAttribute("x", x);
		if (festivalId != null) {
			try {
				model.addAttribute("festival", adminReportService.obtenerFestival(festivalId));
				model.addAttribute("mayoresCanones", adminReportService.mayoresCanones(festivalId, x));
			} catch (IllegalArgumentException ex) {
				model.addAttribute("error", ex.getMessage());
			}
		}
		return ViewRouteHelper.ADMIN_MAYOR_CANON;
	}
}
