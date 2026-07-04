package com.carvimsa.inventario.service;

import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Producto;
import com.carvimsa.inventario.model.TipoMovimiento;
import com.carvimsa.inventario.repository.MovimientoRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Lógica de negocio de generación de reportes de inventario (CU-06).
 */
@Service
public class ReporteService {

    private final MovimientoRepository movimientoRepository;

    public ReporteService(MovimientoRepository movimientoRepository) {
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Resumen de movimientos por producto (entradas, salidas, saldo neto y fecha del último
     * movimiento) dentro de un rango de fechas, usado en el reporte de tipo MOVIMIENTOS.
     */
    public List<ResumenProducto> resumenMovimientos(LocalDate fechaInicio, LocalDate fechaFin) {
        List<MovimientoInventario> movimientos = movimientoRepository.findByFechaBetween(fechaInicio, fechaFin);

        Map<Producto, List<MovimientoInventario>> porProducto = movimientos.stream()
                .collect(Collectors.groupingBy(MovimientoInventario::getProducto, LinkedHashMap::new, Collectors.toList()));

        return porProducto.entrySet().stream()
                .map(entry -> {
                    Producto producto = entry.getKey();
                    List<MovimientoInventario> movs = entry.getValue();

                    int entradas = movs.stream()
                            .filter(m -> m.getTipo() == TipoMovimiento.ENTRADA)
                            .mapToInt(MovimientoInventario::getCantidad)
                            .sum();
                    int salidas = movs.stream()
                            .filter(m -> m.getTipo() == TipoMovimiento.SALIDA)
                            .mapToInt(MovimientoInventario::getCantidad)
                            .sum();
                    LocalDate ultimaFecha = movs.stream()
                            .map(MovimientoInventario::getFecha)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    return new ResumenProducto(producto, entradas, salidas, entradas - salidas, ultimaFecha);
                })
                .collect(Collectors.toList());
    }

    /**
     * DTO de resumen de movimientos por producto para la vista de reportes.
     */
    public record ResumenProducto(Producto producto, int entradas, int salidas, int saldoNeto,
                                   LocalDate fechaUltimoMovimiento) {
    }
}
