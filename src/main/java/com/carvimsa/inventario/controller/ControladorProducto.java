package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.Producto;
import com.carvimsa.inventario.model.TipoProducto;
import com.carvimsa.inventario.repository.ProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Gestión de Productos (CU-09). Rol requerido: Administrador.
 */
@Controller
public class ControladorProducto {

    private final ProductoRepository productoRepository;

    public ControladorProducto(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/admin/productos")
    public String listar(Model model) {
        model.addAttribute("productos", productoRepository.findAll());
        model.addAttribute("producto", new Producto());
        model.addAttribute("tipos", TipoProducto.values());
        return "admin/productos";
    }

    @PostMapping("/admin/productos/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes redirectAttributes) {
        try {
            productoRepository.save(producto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto guardado correctamente.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ya existe un producto con ese código.");
        }
        return "redirect:/admin/productos";
    }
}
