package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone el usuario autenticado como atributo "usuarioActual" en todas las vistas,
 * para mostrar nombre y rol en el topbar (fragments/layout.html).
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioRepository usuarioRepository;

    public GlobalModelAdvice(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @ModelAttribute("usuarioActual")
    public Usuario usuarioActual(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        return usuarioRepository.findByUsername(authentication.getName()).orElse(null);
    }
}
