package com.unla.epicentrogourmet.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;



public class PlatoDTO {

  private Long id;
  @NotBlank(message ="El nombre del plato es obligatorio")
  private String nombre;
  @NotNull(message ="El precio de venta es obligatorio")
  @Positive(message ="El precio de venta debe ser mayor a cero")
  private Double precioVenta;
  @NotNull(message ="El costo de producción es obligatorio")
  @Positive(message ="El costo de producción debe ser mayor a cero")
  private Double costoProduccion;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }
  public Double getPrecioVenta() { return precioVenta; }
  public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
  public Double getCostoProduccion() { return costoProduccion; }
  public void setCostoProduccion(Double costoProduccion) { this.costoProduccion = costoProduccion; }

}
