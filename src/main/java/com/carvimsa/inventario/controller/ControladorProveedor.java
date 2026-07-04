package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.Proveedor;
import com.carvimsa.inventario.repository.ProveedorRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Gestión de Proveedores (CU-08). Rol requerido: Administrador.
 */
@Controller
public class ControladorProveedor {

    private final ProveedorRepository proveedorRepository;

    public ControladorProveedor(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping("/admin/proveedores")
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorRepository.findAll());
        model.addAttribute("proveedor", new Proveedor());
        return "admin/proveedores";
    }

    @PostMapping("/admin/proveedores/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor, RedirectAttributes redirectAttributes) {
        try {
            proveedorRepository.save(proveedor);
            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor guardado correctamente.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", "Ya existe un proveedor con ese RUC.");
        }
        return "redirect:/admin/proveedores";
    }
}
