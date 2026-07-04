package com.carvimsa.inventario.controller;

import com.carvimsa.inventario.model.EstadoAlerta;
import com.carvimsa.inventario.repository.AlertaRepository;
import com.carvimsa.inventario.service.AlertaService;
import java.time.LocalDateTime;
import java.time.YearMonth;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de Gestión de Alertas (CU-05, pantalla 06_alertas.png).
 * Rol requerido: Jefe de Almacén y Administrador (ver SecurityConfig).
 */
@Controller
public class ControladorAlerta {

    private final AlertaService alertaService;
    private final AlertaRepository alertaRepository;

    public ControladorAlerta(AlertaService alertaService, AlertaRepository alertaRepository) {
        this.alertaService = alertaService;
        this.alertaRepository = alertaRepository;
    }

    @GetMapping("/alertas")
    public String listar(Model model) {
        var activas = alertaService.listarActivas();
        var todas = alertaRepository.findAll();

        long sinResolver48h = activas.stream()
                .filter(a -> a.getFechaActivacion().isBefore(LocalDateTime.now().minusHours(48)))
                .count();
        // La entidad Alerta no registra fecha de resolución, solo fecha_activacion;
        // se usa como aproximación: alertas resueltas activadas dentro del mes actual.
        YearMonth mesActual = YearMonth.now();
        long resueltasEsteMes = todas.stream()
                .filter(a -> a.getEstado() == EstadoAlerta.RESUELTA)
                .filter(a -> YearMonth.from(a.getFechaActivacion()).equals(mesActual))
                .count();

        model.addAttribute("alertasActivas", activas);
        model.addAttribute("totalActivas", activas.size());
        model.addAttribute("sinResolver48h", sinResolver48h);
        model.addAttribute("resueltasEsteMes", resueltasEsteMes);

        return "alertas/alertas";
    }

    @PostMapping("/alertas/resolver/{id}")
    public String resolver(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        alertaService.resolver(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Alerta resuelta correctamente.");
        return "redirect:/alertas";
    }
}
