package com.unla.epicentrogourmet.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.unla.epicentrogourmet.enums.Rol;
import com.unla.epicentrogourmet.helpers.ViewRouteHelper;

/** Login y redireccion segun el rol del usuario autenticado. */
@Controller
public class UserController {

	@GetMapping("/login")
	public String login(Model model, @RequestParam(required = false) String error,
			@RequestParam(required = false) String logout) {
		model.addAttribute("error", error);
		model.addAttribute("logout", logout);
		return ViewRouteHelper.USER_LOGIN;
	}

	@GetMapping("/loginsuccess")
	public String loginSuccess(Authentication authentication) {
		boolean esAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(Rol.ADMIN.getAuthority()));
		boolean esResponsable = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(Rol.RESPONSABLE.getAuthority()));
		if (esAdmin) {
			return "redirect:" + ViewRouteHelper.ADMIN_ROOT;
		}
		if (esResponsable) {
			return "redirect:" + ViewRouteHelper.RESPONSABLE_ROOT;
		}
		return "redirect:" + ViewRouteHelper.EMPLEADO_ROOT;
	}
}
