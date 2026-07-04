package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.EstadoReporte;
import com.carvimsa.inventario.model.Reporte;
import com.carvimsa.inventario.model.TipoReporte;
import com.carvimsa.inventario.model.Usuario;
import com.carvimsa.inventario.repository.ReporteRepository;
import com.carvimsa.inventario.repository.UsuarioRepository;
import com.carvimsa.inventario.service.ReporteService;
import java.time.LocalDate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador de Generar Reportes de Inventario (CU-06, pantalla 07_reportes.png).
 * Rol requerido: Jefe de Almacén y Administrador (ver SecurityConfig).
 */
@Controller
public class ControladorReporte {

    private final ReporteService reporteService;
    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;

    public ControladorReporte(ReporteService reporteService,
                               ReporteRepository reporteRepository,
                               UsuarioRepository usuarioRepository) {
        this.reporteService = reporteService;
        this.reporteRepository = reporteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/reportes")
    public String formulario(Model model) {
        model.addAttribute("tipos", TipoReporte.values());
        model.addAttribute("fechaHoy", LocalDate.now());
        return "reportes/reportes";
    }

    @PostMapping("/reportes/generar")
    public String generar(@RequestParam TipoReporte tipo,
                           @RequestParam LocalDate fechaInicio,
                           @RequestParam LocalDate fechaFin,
                           Authentication authentication,
                           Model model) {

        model.addAttribute("tipos", TipoReporte.values());
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("resultados", reporteService.resumenMovimientos(fechaInicio, fechaFin));

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuario);
        reporte.setTipo(tipo);
        reporte.setFechaInicio(fechaInicio);
        reporte.setFechaFin(fechaFin);
        reporte.setEstado(EstadoReporte.GENERADO);
        reporteRepository.save(reporte);

        return "reportes/reportes";
    }
}
