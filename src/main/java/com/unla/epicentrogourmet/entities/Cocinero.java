package com.unla.epicentrogourmet.entities;

import com.unla.epicentrogourmet.enums.RolCocina;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Personal de cocina: la "tarea especial" es el rol (cocinero, ayudante, lavaplatos). */
@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Personal {

	@Enumerated(EnumType.STRING)
	private RolCocina rolCocina;
}
