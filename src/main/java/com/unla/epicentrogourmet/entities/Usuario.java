package com.unla.epicentrogourmet.entities;

import com.unla.epicentrogourmet.enums.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Usuario del sistema. El administrador no tiene Personal asociado. */
@Entity
@Getter @Setter @NoArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String username;

	/** Hash BCrypt, nunca la contrasenia en texto plano. */
	@Column(nullable = false, length = 60)
	private String password;

	private boolean enabled = true;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Rol rol;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "personal_id")
	private Personal personal;
}
