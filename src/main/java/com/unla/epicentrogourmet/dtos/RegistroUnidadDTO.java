package com.unla.epicentrogourmet.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistroUnidadDTO {

    @NotNull(message = "Debe seleccionar un festival")
    private Long festivalId;
    @NotBlank(message = "El nombre comercial es obligatorio")
    private String nombreComercial;
    @NotNull(message = "La superficie es obligatoria")
    @Positive(message = "La superficie debe ser mayor a 0")
    private Double superficie;
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{8}$", message = "El código debe tener 2 letras mayúsculas seguidas de 8 dígitos")
    private String codigo;
    private String tipoUnidad;
    private String patente;
    private Boolean requiereElectricidad;
    private Integer minutosMontaje;
    private Integer carpas;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    @NotNull(message = "El DNI es obligatorio")
    @Min(value=1000000,message="dni inválido")
    private Integer dni;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
    @NotNull(message = "La fecha de ingreso es obligatoria")
    private LocalDate fechaIngreso;
    @NotBlank (message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;
    @NotBlank(message = "El tipo de personal es obligatorio")
    private String tipoPersonal;
    @NotBlank(message = "El usuario es obligatorio")
    private String userName;
    @NotBlank(message = "El password es obligatorio")
    private String password;


}
