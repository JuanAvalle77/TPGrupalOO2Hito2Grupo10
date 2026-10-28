package com.unla.epicentrogourmet.controllers;

import com.unla.epicentrogourmet.dtos.RegistroUnidadDTO;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.services.implementation.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registro")
public class RegistroController {

@Autowired private RegistroService registroService;
@Autowired private IFestivalRepository festivalRepository;

@GetMapping
public String mostrarFormulario(Model model){

    model.addAttribute("registroDTO",new RegistroUnidadDTO());
    model.addAttribute("festivales",festivalRepository.findAll());

    return "registro";

}

    @PostMapping
    public String procesarRegistro(@Valid @ModelAttribute("registroDTO") RegistroUnidadDTO dto,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("festivales", festivalRepository.findAll());
            return "registro";
        }

        try {
            registroService.registrarUnidadConResponsable(dto);
            return "redirect:/login?registrado=true";
        } catch (Exception e) {
            e.printStackTrace(); // <--- Muestra en la consola de VS Code el error exacto
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            model.addAttribute("festivales", festivalRepository.findAll());
            return "registro";
        }
    }

}
