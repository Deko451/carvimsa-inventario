package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.Alerta;
import com.carvimsa.inventario.model.EstadoAlerta;
import com.carvimsa.inventario.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    List<Alerta> findByEstado(EstadoAlerta estado);

    List<Alerta> findByProducto(Producto producto);

    List<Alerta> findByProductoAndEstado(Producto producto, EstadoAlerta estado);
}
