package com.unla.epicentrogourmet.controllers;

import com.unla.epicentrogourmet.dtos.PlatoEstrellaDTO;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.services.implementation.PlatoEstrellaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping ("/responsable/plato-estrella")
public class PlatoEstrellaController {

@Autowired private PlatoEstrellaService platoEstrellaService;
@Autowired private IFestivalRepository festivalRepository;

@GetMapping
public String verPlatoEstrella(@RequestParam (value = "festivalId",required=false)Long festivalId, Model model, Principal principal){

    model.addAttribute("festivales",festivalRepository.findAll());
    model.addAttribute("festivalSeleccionado",festivalId);

    if(festivalId!=null){

        try{
            PlatoEstrellaDTO platoEstrella=platoEstrellaService.obtenerPlatoEstrella(principal.getName(),festivalId);
        model.addAttribute("platoEstrella",platoEstrella);
        model.addAttribute("busquedaRealizada",true);
        }
        catch(Exception e){
          model.addAttribute("error",e.getMessage());
        }
    }

    return "responsable/plato_estrella";
}
}
