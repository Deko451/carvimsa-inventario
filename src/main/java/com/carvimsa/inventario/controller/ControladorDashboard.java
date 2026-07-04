package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.EstadoAlerta;
import com.carvimsa.inventario.model.EstadoPedido;
import com.carvimsa.inventario.repository.AlertaRepository;
import com.carvimsa.inventario.repository.MovimientoRepository;
import com.carvimsa.inventario.repository.PedidoRepository;
import com.carvimsa.inventario.repository.ProductoRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador del Dashboard: muestra KPIs y últimos movimientos (pantalla 02_dashboard.png).
 */
@Controller
public class ControladorDashboard {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final AlertaRepository alertaRepository;
    private final PedidoRepository pedidoRepository;

    public ControladorDashboard(ProductoRepository productoRepository,
                                 MovimientoRepository movimientoRepository,
                                 AlertaRepository alertaRepository,
                                 PedidoRepository pedidoRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.alertaRepository = alertaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long productosActivos = productoRepository.findByEstado(true).size();
        long movimientosHoy = movimientoRepository.findByFechaBetween(LocalDate.now(), LocalDate.now()).size();
        long alertasActivas = alertaRepository.findByEstado(EstadoAlerta.ACTIVA).size();
        long pedidosPendientes = pedidoRepository.findByEstado(EstadoPedido.PENDIENTE).size();

        model.addAttribute("productosActivos", productosActivos);
        model.addAttribute("movimientosHoy", movimientosHoy);
        model.addAttribute("alertasActivas", alertasActivas);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("ultimosMovimientos", movimientoRepository.findTop10ByOrderByCreatedAtDesc());

        return "dashboard";
    }
}
