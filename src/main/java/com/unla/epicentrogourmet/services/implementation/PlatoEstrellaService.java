package com.unla.epicentrogourmet.services.implementation;


import com.unla.epicentrogourmet.dtos.PlatoEstrellaDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.repositories.IDetallePedidoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatoEstrellaService {
    @Autowired  private IDetallePedidoRepository detallePedidoRepository;
    @Autowired  private IUnidadVentaRepository unidadVentaRepository;
    @Autowired  private IUsuarioRepository usuarioRepository;

    public PlatoEstrellaDTO obtenerPlatoEstrella(String username,Long festivalId)throws Exception {

     Usuario usuario=usuarioRepository.findByUsername(username)
             .orElseThrow(()->new Exception("Usuario no encontrado"));
     List<UnidadVenta> unidades=unidadVentaRepository.findByResponsableId(usuario.getPersonal().getId());

     if(unidades.isEmpty()){
         throw new Exception("El responsable no tiene una unidad de venta asignada");
     }

     UnidadVenta unidad=unidades.get(0);
     List<Object[]> resultados=detallePedidoRepository.findPlatoEstrellaByUnidadYFestival(unidad.getId(),festivalId);

    if(resultados.isEmpty()){

        return null;
    }

        Object[] fila = resultados.get(0);

        String nombre = (String) fila[0];
        double precioVenta = ((Number) fila[1]).doubleValue();
        long unidadesVendidas = ((Number) fila[2]).longValue();
        long cantidadPedidos = ((Number) fila[3]).longValue();

        return new PlatoEstrellaDTO(nombre, precioVenta, unidadesVendidas, cantidadPedidos);
    }
}
