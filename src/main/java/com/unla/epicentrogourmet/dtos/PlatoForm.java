package com.unla.epicentrogourmet.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class PlatoForm {

	private Long unidadId;

	@NotBlank(message = "Ingresá el nombre del plato")
	@Size(max = 120, message = "El nombre no puede superar 120 caracteres")
	private String nombre;

	@NotNull(message = "Ingresá el precio de venta")
	@Positive(message = "El precio de venta debe ser mayor a cero")
	private Double precioVenta;

	@NotNull(message = "Ingresá el costo de producción")
	@Positive(message = "El costo de producción debe ser mayor a cero")
	private Double costoProduccion;
}
