package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.MovimientoInventario;
import com.carvimsa.inventario.model.Producto;
import com.carvimsa.inventario.model.TipoMovimiento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByProducto(Producto producto);

    List<MovimientoInventario> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<MovimientoInventario> findByTipo(TipoMovimiento tipo);

    List<MovimientoInventario> findTop10ByOrderByCreatedAtDesc();
}
