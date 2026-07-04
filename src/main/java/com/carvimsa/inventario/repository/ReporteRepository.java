package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.Reporte;
import com.carvimsa.inventario.model.TipoReporte;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    List<Reporte> findByTipo(TipoReporte tipo);

    List<Reporte> findByFechaInicioGreaterThanEqual(LocalDate fechaInicio);
}
