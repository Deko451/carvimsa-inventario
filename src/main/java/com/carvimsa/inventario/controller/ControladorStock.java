package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de Consultar Stock (CU-04, pantalla 04_consultar_stock.png).
 */
@Controller
public class ControladorStock {

    private final ProductoRepository productoRepository;

    public ControladorStock(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping("/stock")
    public String stock(Model model) {
        model.addAttribute("productos", productoRepository.findByEstado(true));
        return "inventario/stock";
    }
}
