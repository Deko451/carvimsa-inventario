package com.carvimsa.inventario.service;

import com.carvimsa.inventario.model.Alerta;
import com.carvimsa.inventario.model.EstadoAlerta;
import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Producto;
import com.carvimsa.inventario.model.TipoMovimiento;
import com.carvimsa.inventario.repository.AlertaRepository;
import com.carvimsa.inventario.repository.MovimientoRepository;
import com.carvimsa.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio de ingresos y salidas de inventario (CU-02, CU-03).
 * Regla crítica: toda SALIDA que deje el stock por debajo del mínimo
 * dispara automáticamente una Alerta en estado ACTIVA.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final AlertaRepository alertaRepository;

    public InventarioService(ProductoRepository productoRepository,
                              MovimientoRepository movimientoRepository,
                              AlertaRepository alertaRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Transactional
    public void registrarIngreso(MovimientoInventario movimiento) {
        Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        producto.setStockActual(producto.getStockActual() + movimiento.getCantidad());
        productoRepository.save(producto);

        movimiento.setProducto(producto);
        movimiento.setTipo(TipoMovimiento.ENTRADA);
        movimientoRepository.save(movimiento);
    }

    @Transactional
    public void registrarSalida(MovimientoInventario movimiento) {
        Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (movimiento.getCantidad() == null || movimiento.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }

        if (producto.getStockActual() < movimiento.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStockActual());
        }

        producto.setStockActual(producto.getStockActual() - movimiento.getCantidad());
        productoRepository.save(producto);

        movimiento.setProducto(producto);
        movimiento.setTipo(TipoMovimiento.SALIDA);
        movimientoRepository.save(movimiento);

        // Regla crítica: alerta automática si el stock cae bajo el mínimo (CU-03 -> CU-05)
        if (producto.getStockActual() < producto.getStockMinimo()) {
            Alerta alerta = new Alerta();
            alerta.setProducto(producto);
            alerta.setStockAlMomento(producto.getStockActual());
            alerta.setStockMinimo(producto.getStockMinimo());
            alerta.setEstado(EstadoAlerta.ACTIVA);
            alertaRepository.save(alerta);
        }
    }
}
