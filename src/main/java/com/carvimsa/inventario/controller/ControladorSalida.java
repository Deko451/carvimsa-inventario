package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.ProductoRepository;
import com.carvimsa.inventario.repository.UsuarioRepository;
import com.carvimsa.inventario.service.InventarioService;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Registrar Salida de Inventario (CU-03, pantalla 05_registrar_salida.png).
 */
@Controller
public class ControladorSalida {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    public ControladorSalida(ProductoRepository productoRepository,
                              UsuarioRepository usuarioRepository,
                              InventarioService inventarioService) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/salidas/nuevo")
    public String nuevo(Model model) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDate.now());
        model.addAttribute("movimiento", movimiento);
        model.addAttribute("productos", productoRepository.findByEstado(true));
        return "inventario/salida";
    }

    @PostMapping("/salidas/guardar")
    public String guardar(@ModelAttribute MovimientoInventario movimiento,
                           @RequestParam("producto.id") Integer idProducto,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        var producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        movimiento.setProducto(producto);

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        movimiento.setUsuario(usuario);

        try {
            inventarioService.registrarSalida(movimiento);
            redirectAttributes.addFlashAttribute("mensajeExito", "Salida registrada correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }

        return "redirect:/salidas/nuevo";
    }
}
