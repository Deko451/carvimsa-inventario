package com.carvimsa.inventario.repository;

import com.carvimsa.inventario.model.DetallePedido;
import com.carvimsa.inventario.model.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedido(Pedido pedido);
}
