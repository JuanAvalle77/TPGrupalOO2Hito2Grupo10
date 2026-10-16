package com.unla.epicentrogourmet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.unla.epicentrogourmet.helpers.ViewRouteHelper;
import com.unla.epicentrogourmet.services.IFestivalService;

/** Pagina de inicio (publica): informacion de los festivales. */
@Controller
public class HomeController {

	private final IFestivalService festivalService;

	public HomeController(IFestivalService festivalService) {
		this.festivalService = festivalService;
	}

	@GetMapping({ "/", "/index" })
	public ModelAndView index() {
		ModelAndView mAV = new ModelAndView(ViewRouteHelper.HOME_INDEX);
		mAV.addObject("festivales", festivalService.getAll());
		return mAV;
	}
}
