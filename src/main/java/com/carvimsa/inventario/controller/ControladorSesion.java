package com.carvimsa.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de sesión: pantalla de login (CU-01) y dashboard principal.
 */
@Controller
public class ControladorSesion {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }
}
