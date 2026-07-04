package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.RolRepository;
import com.carvimsa.inventario.repository.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Gestión de Usuarios y Roles (CU-10). Rol requerido: Administrador.
 */
@Controller
public class ControladorUsuario {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public ControladorUsuario(UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolRepository.findAll());
        return "admin/usuarios";
    }

    @PostMapping("/admin/usuarios/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                           @RequestParam("rol.id") Integer idRol,
                           RedirectAttributes redirectAttributes) {

        var rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        try {
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario guardado correctamente.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ya existe un usuario con ese nombre de usuario.");
        }
        return "redirect:/admin/usuarios";
    }
}
