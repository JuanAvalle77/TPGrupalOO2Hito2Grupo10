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
@RequestMapping("/admin/ranking")
public class AdminRankingController {

	private final IFestivalService festivalService;
	private final IAdminReportService adminReportService;

	public AdminRankingController(IFestivalService festivalService, IAdminReportService adminReportService) {
		this.festivalService = festivalService;
		this.adminReportService = adminReportService;
	}

	@GetMapping
	public String ranking(@RequestParam(required = false) Long festivalId, Model model) {
		model.addAttribute("festivales", festivalService.getAll());
		if (festivalId != null) {
			try {
				model.addAttribute("festival", adminReportService.obtenerFestival(festivalId));
				model.addAttribute("ranking", adminReportService.ranking(festivalId));
			} catch (IllegalArgumentException ex) {
				model.addAttribute("error", ex.getMessage());
			}
		}
		return ViewRouteHelper.ADMIN_RANKING;
	}
}
