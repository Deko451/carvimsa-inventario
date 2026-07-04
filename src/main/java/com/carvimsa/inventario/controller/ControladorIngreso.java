package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.ProductoRepository;
import com.carvimsa.inventario.repository.ProveedorRepository;
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
 * Controlador de Registrar Ingreso de Inventario (CU-02, pantalla 03_registrar_ingreso.png).
 */
@Controller
public class ControladorIngreso {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;

    public ControladorIngreso(ProductoRepository productoRepository,
                               ProveedorRepository proveedorRepository,
                               UsuarioRepository usuarioRepository,
                               InventarioService inventarioService) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.inventarioService = inventarioService;
    }

    @GetMapping("/ingresos/nuevo")
    public String nuevo(Model model) {
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setFecha(LocalDate.now());
        model.addAttribute("movimiento", movimiento);
        model.addAttribute("productos", productoRepository.findByEstado(true));
        model.addAttribute("proveedores", proveedorRepository.findByEstado(true));
        return "inventario/ingreso";
    }

    @PostMapping("/ingresos/guardar")
    public String guardar(@ModelAttribute MovimientoInventario movimiento,
                           @RequestParam("producto.id") Integer idProducto,
                           @RequestParam(value = "proveedor.id", required = false) Integer idProveedor,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        var producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        movimiento.setProducto(producto);

        if (idProveedor != null) {
            proveedorRepository.findById(idProveedor).ifPresent(movimiento::setProveedor);
        }

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        movimiento.setUsuario(usuario);

        try {
            inventarioService.registrarIngreso(movimiento);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ingreso registrado correctamente.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }

        return "redirect:/ingresos/nuevo";
    }
}
