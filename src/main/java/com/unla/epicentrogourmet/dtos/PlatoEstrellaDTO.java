package com.unla.epicentrogourmet.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlatoEstrellaDTO {

    private String nombre;
    private double precioVenta;
    private long unidadesVendidas;
    private long cantidadPedidos;

    public PlatoEstrellaDTO(){
    };

    public PlatoEstrellaDTO( String nombre,
     double precioVenta,
     long unidadesVendidas,
     long cantidadPedidos){

        this.nombre=nombre;
        this.precioVenta=precioVenta;
        this.unidadesVendidas=unidadesVendidas;
        this.cantidadPedidos=cantidadPedidos;

    }



}
