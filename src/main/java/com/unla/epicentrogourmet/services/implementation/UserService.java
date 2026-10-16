package com.unla.epicentrogourmet.services.implementation;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;

/** Carga el usuario desde la base para Spring Security. Un usuario tiene un unico rol. */
@Service("userService")
public class UserService implements UserDetailsService {

	private final IUsuarioRepository usuarioRepository;

	public UserService(IUsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
		return new User(usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), true, true, true,
				List.of(new SimpleGrantedAuthority(usuario.getRol().getAuthority())));
	}
}
