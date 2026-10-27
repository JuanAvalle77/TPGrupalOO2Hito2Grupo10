package com.unla.epicentrogourmet.controllers;

import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.services.implementation.PlatoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/responsable/platos")
public class PlatoController {

    @Autowired private PlatoService platoService;

    @GetMapping
    public String listarplatos(Model model, Principal principal){

        try {
           String username=principal.getName();
           model.addAttribute("platos",platoService.obtenerPlatosPorUsername(username));
        }
        catch(Exception e){
            model.addAttribute("error",e.getMessage());
        }
        return "responsable/platos/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model){

        model.addAttribute("plato",new Plato());

        return "responsable/platos/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarPlato(@Valid @ModelAttribute("plato") Plato plato, BindingResult result,Principal principal,Model model){

        if(plato.getPrecioVenta() >0 && plato.getCostoProduccion() >0){

           if(plato.getPrecioVenta() <= plato.getCostoProduccion()){

               result.rejectValue("precioVenta","error.plato","El precio de venta debe ser mayor al costo de producción");
           }
        }
        if(result.hasErrors()){

            return "responsable/platos/formulario";
        }
       try{
           platoService.guardarPlatoParaResponsable(plato,principal.getName());
           return "redirect:/responsable/platos";
       }
       catch(Exception e){

           model.addAttribute("error",e.getMessage());
           return "responsable/platos/formulario";
        }

    }




}
