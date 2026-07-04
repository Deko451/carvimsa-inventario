package com.carvimsa.inventario.service;

import com.carvimsa.inventario.model.Alerta;
import com.carvimsa.inventario.model.EstadoAlerta;
import com.carvimsa.inventario.repository.AlertaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio de gestión de alertas de stock mínimo (CU-05).
 */
@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public List<Alerta> listarActivas() {
        return alertaRepository.findByEstado(EstadoAlerta.ACTIVA);
    }

    public List<Alerta> listarTodas() {
        return alertaRepository.findAll();
    }

    @Transactional
    public void resolver(Integer idAlerta) {
        Alerta alerta = alertaRepository.findById(idAlerta)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        alerta.setEstado(EstadoAlerta.RESUELTA);
        alertaRepository.save(alerta);
    }
}
