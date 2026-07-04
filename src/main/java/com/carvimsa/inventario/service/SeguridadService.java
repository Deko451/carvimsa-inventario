package com.carvimsa.inventario.service;

import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.UsuarioRepository;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación de UserDetailsService para Spring Security: autentica contra
 * la tabla usuario y expone el rol como authority (ROLE_&lt;nombreRol&gt;).
 */
@Service
public class SeguridadService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public SeguridadService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .disabled(!Boolean.TRUE.equals(usuario.getEstado()))
                .authorities(Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol().getNombre())))
                .build();
    }
}
