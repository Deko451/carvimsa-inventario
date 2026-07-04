package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    Optional<Proveedor> findByRuc(String ruc);

    java.util.List<Proveedor> findByEstado(Boolean estado);
}
